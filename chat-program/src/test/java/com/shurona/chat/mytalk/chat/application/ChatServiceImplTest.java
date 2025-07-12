package com.shurona.chat.mytalk.chat.application;

import static com.shurona.chat.mytalk.chat.common.exception.ChatErrorCode.PRIVATE_CHAT_FRIEND_REQUIRED;
import static com.shurona.chat.mytalk.chat.common.exception.ChatErrorCode.USER_NOT_INCLUDE_ROOM;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.shurona.chat.mytalk.chat.common.exception.ChatException;
import com.shurona.chat.mytalk.chat.domain.model.ChatLog;
import com.shurona.chat.mytalk.chat.domain.model.ChatRoom;
import com.shurona.chat.mytalk.chat.domain.type.ChatContentType;
import com.shurona.chat.mytalk.chat.domain.type.RoomType;
import com.shurona.chat.mytalk.chat.presentation.dto.ChatLogResponseDto;
import com.shurona.chat.mytalk.config.TestContainerConfig;
import com.shurona.chat.mytalk.friend.application.FriendService;
import com.shurona.chat.mytalk.friend.domain.model.Friend;
import com.shurona.chat.mytalk.user.application.UserService;
import com.shurona.chat.mytalk.user.domain.model.User;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(TestContainerConfig.class)
@Transactional
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ChatServiceImplTest {

    @Autowired
    private UserService userService;

    @Autowired
    private FriendService friendService;

    @Autowired
    private ChatService chatService;

    private User userA;
    private User userB;
    private User userC;
    private Friend friend;

    @BeforeEach
    void setUp() {
        // 사용자 A 생성
        String username = "username";
        String password = "password";
        String description = "description";
        String phoneNumber = "010-202-2020";
        Long userId = userService.saveUser(username, password, description, phoneNumber);
        userA = userService.findUserById(userId);

        // 사용자 B 생성
        String username2 = "username2";
        String password2 = "password2";
        String description2 = "description2";
        String phoneNumber2 = "010-202-2022";
        Long userId2 = userService.saveUser(username2, password2, description2, phoneNumber2);
        userB = userService.findUserById(userId2);

        String username3 = "username3";
        String password3 = "password3";
        String description3 = "description3";
        String phoneNumber3 = "010-202-2023";
        Long userId3 = userService.saveUser(username3, password3, description3, phoneNumber3);
        userC = userService.findUserById(userId3);

        // 친구 관계 생성 (아직 수락 전)
        friend = friendService.saveFriend(userA, userB);
    }

    @Test
    void 친구가_아닌_상태에서_채팅방_생성시_예외발생() {
        // when & then
        assertThatThrownBy(
            () -> chatService.createChatRoom(userA, List.of(userB), RoomType.PRIVATE,
                "secretRoom"))
            .isInstanceOf(ChatException.class)
            .hasMessage(PRIVATE_CHAT_FRIEND_REQUIRED.getMessage());
    }

    @Test
    void 친구_수락_후_채팅방_생성_성공() {
        // given
        friend.acceptFriendRequest();

        // when
        ChatRoom chatRoom = chatService.createChatRoom(userA, List.of(userB), RoomType.PRIVATE,
            "secretRoom");

        // then
        assertThat(chatRoom.getName()).isEqualTo("secretRoom");
    }

    @Test
    void 친구_수락_후_채팅방_반복_생성_확인() {
        // given
        friend.acceptFriendRequest();

        // when
        chatService.createChatRoom(userA, List.of(userB), RoomType.PRIVATE,
            "secretRoom");
        chatService.createChatRoom(userA, List.of(userB), RoomType.PRIVATE,
            "secretRoom");
        ChatRoom chatRoom = chatService.createChatRoom(userA, List.of(userB), RoomType.PRIVATE,
            "secretRoom");

        // then
        assertThat(chatRoom.getName()).isEqualTo("secretRoom");
    }

    @Test
    void 채팅방_목록_조회_정상동작() {
        // given
        friend.acceptFriendRequest();
        chatService.createChatRoom(userA, List.of(userB), RoomType.PRIVATE, "secretRoom");

        // when
        List<ChatRoom> chatRoomListByUserA = chatService.findChatRoomListByUser(userA);
        List<ChatRoom> chatRoomListByUserB = chatService.findChatRoomListByUser(userB);

        // then
        assertThat(chatRoomListByUserA.size()).isEqualTo(1);
        assertThat(chatRoomListByUserB.size()).isEqualTo(1);
    }

    @Test
    void 채팅창_기록_성공_조회() {
        // given
        friend.acceptFriendRequest();
        ChatRoom room = chatService.createChatRoom(userA, List.of(userB), RoomType.PRIVATE,
            "secretRoom");
        String firstChatData = "안녕하세요 B";

        chatService.writeChat(room, userA, "안녕하세요 A", ChatContentType.TEXT);
        chatService.writeChat(room, userB, firstChatData, ChatContentType.TEXT);

        // when
        Page<ChatLog> chatLogsWithPage = chatService.readChatLog(userA, room,
            PageRequest.of(0, 20));
        Map<Long, Long> userRecentReadMap = chatService.findUserRecentReadMap(room);
        List<ChatLogResponseDto> chatLogs = mapToResponseDtos(chatLogsWithPage, userRecentReadMap);

        // then
        room = chatService.findChatRoomById(room.getId());
        assertThat(room.getLastMessage()).isEqualTo(firstChatData);
        assertThat(chatLogs.size()).isEqualTo(2);
        assertThat(chatLogs.get(0).content()).isEqualTo(firstChatData);
        assertThat(chatLogs.get(0).unreadCount()).isEqualTo(1);

        // 같은 유저가 반복해서 채팅 읽었을 때 숫자 유지 확인
        List<ChatLogResponseDto> chatLogsOneMore = mapToResponseDtos(chatLogsWithPage,
            userRecentReadMap);
        assertThat(chatLogsOneMore.get(0).unreadCount()).isEqualTo(1);

        // 안 읽었던 유저가 채팅 읽었을 때 숫자 확인
        Page<ChatLog> chatLogsForUserB = chatService.readChatLog(userB, room,
            PageRequest.of(0, 20));
        Map<Long, Long> userRecentReadMapForUserB = chatService.findUserRecentReadMap(room);
        List<ChatLogResponseDto> chatLogsForUserBResponse = mapToResponseDtos(chatLogsForUserB,
            userRecentReadMapForUserB);
        assertThat(chatLogsForUserBResponse.get(0).unreadCount()).isEqualTo(0);
    }

    @Test
    void 채팅창_기록_검증_실패() {
        friend.acceptFriendRequest();
        ChatRoom room = chatService
            .createChatRoom(userA, List.of(userB), RoomType.PRIVATE, "secretRoom");

        assertThatThrownBy(() -> {
            chatService.writeChat(room, userC, "안녕하세요", ChatContentType.TEXT);
        })
            .isInstanceOf(ChatException.class)
            .hasMessage(USER_NOT_INCLUDE_ROOM.getMessage());

    }

    /**
     * chatLogDto에 unreadCount를 추가해준다.
     */
    private List<ChatLogResponseDto> mapToResponseDtos
    (Page<ChatLog> chatLogs, Map<Long, Long> userRecentReadMap) {
        return chatLogs.stream().map(log -> {
            int unreadCount = (int) userRecentReadMap.values().stream()
                .filter(recentReadId -> recentReadId < log.getId())
                .count();
            return ChatLogResponseDto.of(log, unreadCount);
        }).toList();
    }

}