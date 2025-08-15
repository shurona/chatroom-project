package com.shurona.chat.mytalk.chat.application.cache;

import static org.assertj.core.api.Assertions.assertThat;

import com.shurona.chat.mytalk.chat.application.cache.dtos.ChatCacheRedisInfo;
import com.shurona.chat.mytalk.chat.domain.model.ChatLog;
import com.shurona.chat.mytalk.chat.domain.model.ChatRoom;
import com.shurona.chat.mytalk.chat.domain.model.ChatUser;
import com.shurona.chat.mytalk.chat.domain.type.ChatContentType;
import com.shurona.chat.mytalk.chat.domain.type.RoomType;
import com.shurona.chat.mytalk.config.TestContainerConfig;
import com.shurona.chat.mytalk.user.domain.model.User;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.util.ReflectionUtils;

/**
 * 실제로 Redis에서 ZSet 및 조회를 테스트 하기 위해서 통합테스트로 진행
 * Mock Test는 Memory에서 해보자
 */
//@ExtendWith(MockitoExtension.class)
@ExtendWith(TestContainerConfig.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@EmbeddedKafka
class ChatCacheRedisInfoTest {

    @Autowired
    private ChatCacheRedisInfo chatCacheRedisInfo;

    private User user1;
    private User user2;
    private User user3;

    private ChatRoom chatRoom;

    @BeforeEach
    public void setUp() throws Exception {
        // 테스트를 위한 초기 설정을 수행합니다.
        user1 = User.createUser("testUser1", "testUser1", "", "010-1234-5678");
        user2 = User.createUser("testUser2", "testUser2", "", "010-9876-5432");
        user3 = User.createUser("testUser3", "testUser3", "", "010-1111-2222");
        setEntityId(user1, 1L);
        setEntityId(user2, 2L);
        setEntityId(user3, 3L);

        chatRoom = ChatRoom.createChatRoom(
            "Test Room", RoomType.PRIVATE, user1, List.of(user2, user3));
        setEntityId(chatRoom, 1L);

        chatRoom.addChatLog(user1, "hello", ChatContentType.TEXT);
        chatRoom.addChatLog(user1, "how are you?", ChatContentType.TEXT);
        chatRoom.addChatLog(user2, "hi", ChatContentType.TEXT);

        for (ChatLog chatLog : chatRoom.getChatLogList()) {
            setEntityId(chatLog, (long) chatRoom.getChatLogList().indexOf(chatLog) + 1);
        }

    }

    @Test
    public void 최근_메시지_조회() throws NoSuchFieldException, IllegalAccessException {

        ChatLog logFirst = ChatLog.createLog(chatRoom, user1, "content", ChatContentType.TEXT);
        setEntityCreateTime(logFirst, LocalDateTime.now());

        ChatLog logTwo = ChatLog.createLog(chatRoom, user1, "content-2", ChatContentType.TEXT);
        setEntityCreateTime(logTwo, LocalDateTime.now());

        ChatLog logThird = ChatLog.createLog(chatRoom, user1, "content-3", ChatContentType.TEXT);
        setEntityCreateTime(logThird, LocalDateTime.now());

        // 처음 업데이트 때는 true
        boolean isUpdate = chatCacheRedisInfo.checkLastMessageUpdate(chatRoom, logTwo);
        assertThat(isUpdate).isTrue();

        // 이전 데이터 업데이트의 경우에는 false
        isUpdate = chatCacheRedisInfo.checkLastMessageUpdate(chatRoom, logFirst);
        assertThat(isUpdate).isFalse();

        // 이미 있는 상황에서 최근거 추가면 true
        isUpdate = chatCacheRedisInfo.checkLastMessageUpdate(chatRoom, logThird);
        assertThat(isUpdate).isTrue();
    }

    @Test
    public void 안읽은_유저_계산() {

        List<ChatUser> chatUserList = chatRoom.getChatUserList();

        chatUserList.getFirst().updateRecentRead(2L); // user1은 2번째 메시지까지 읽음
        chatUserList.get(1).updateRecentRead(3L); // user2는 3번째 메시지까지 읽음
        chatUserList.get(2).updateRecentRead(0L); // user3은 아무것도 읽지 않음

        // 업데이트
        chatCacheRedisInfo.calculateUnreadCount(
            chatRoom, chatRoom.getChatLogList(), chatRoom.getChatUserList());

        Map<Long, Integer> unreadCountByChatRoomId = chatCacheRedisInfo.getUnreadCountByChatRoomId(
            chatRoom.getId());

        assertThat(unreadCountByChatRoomId.get(1L)).isEqualTo(1); // 첫 번째 메시지: user3가 읽지 않음
        assertThat(unreadCountByChatRoomId.get(2L)).isEqualTo(1); // 두 번째 메시지: user3가 읽지 않음
        assertThat(unreadCountByChatRoomId.get(3L)).isEqualTo(2); // 세 번째 메시지: user1, user3가 읽지 않음

        // 일부 메시지 범위 조회 테스트
        Map<Long, Integer> unreadCountByChatRoomIdAndLogRange = chatCacheRedisInfo.getUnreadCountByChatRoomIdAndLogRange(
            chatRoom.getId(), 1L, 2L);
        assertThat(unreadCountByChatRoomIdAndLogRange.size()).isEqualTo(2);
    }


    // 테스트를 위한 아이디 설정을 위한 메소드
    private void setEntityId(Object entity, Long id)
        throws NoSuchFieldException, IllegalAccessException {
        Field idField = entity.getClass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(entity, id);
    }

    private void setEntityCreateTime(Object entity, LocalDateTime createdAt) {
        Field field = ReflectionUtils.findField(entity.getClass(), "createdAt");
        if (field != null) {
            field.setAccessible(true);
            ReflectionUtils.setField(field, entity, createdAt);
        }
    }


}