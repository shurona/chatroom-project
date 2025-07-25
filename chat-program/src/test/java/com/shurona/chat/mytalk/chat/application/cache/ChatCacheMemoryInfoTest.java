package com.shurona.chat.mytalk.chat.application.cache;

import static org.assertj.core.api.Assertions.assertThat;

import com.shurona.chat.mytalk.chat.domain.model.ChatLog;
import com.shurona.chat.mytalk.chat.domain.model.ChatRoom;
import com.shurona.chat.mytalk.chat.domain.model.ChatUser;
import com.shurona.chat.mytalk.chat.domain.type.ChatContentType;
import com.shurona.chat.mytalk.chat.domain.type.RoomType;
import com.shurona.chat.mytalk.user.domain.model.User;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ChatCacheMemoryInfoTest {

    @InjectMocks
    private ChatCacheMemoryInfo chatCacheMemoryInfo;

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
    public void testCalculateUnreadCount() {
        // Given
        List<ChatUser> chatUserList = chatRoom.getChatUserList();
        // chatUserList에 각 유저의 lastReadMessageId를 설정합니다.
        chatUserList.getFirst().updateRecentRead(2L); // user1은 2번째 메시지까지 읽음
        chatUserList.get(1).updateRecentRead(3L); // user2는 3번째 메시지까지 읽음
        chatUserList.get(2).updateRecentRead(0L); // user3은 아무것도 읽지 않음

        // When
        chatCacheMemoryInfo.calculateUnreadCount(chatRoom, chatRoom.getChatLogList(), chatUserList);

        // Then
        Map<Long, Integer> unreadCountByChatRoomId = chatCacheMemoryInfo.getUnreadCountByChatRoomId(
            chatRoom.getId());

        assertThat(unreadCountByChatRoomId.get(1L)).isEqualTo(1); // 첫 번째 메시지: user3가 읽지 않음
        assertThat(unreadCountByChatRoomId.get(2L)).isEqualTo(1); // 두 번째 메시지: user3가 읽지 않음
        assertThat(unreadCountByChatRoomId.get(3L)).isEqualTo(2); // 세 번째 메시지: user1, user3가 읽지 않음

        // 일부 메시지 범위 조회 테스트
        Map<Long, Integer> unreadCountByChatRoomIdAndLogRange = chatCacheMemoryInfo.getUnreadCountByChatRoomIdAndLogRange(
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

}