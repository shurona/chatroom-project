package com.shurona.chat.mytalk.chat.presentation.rest;

import com.shurona.chat.mytalk.chat.application.ChatService;
import com.shurona.chat.mytalk.chat.common.exception.ChatErrorCode;
import com.shurona.chat.mytalk.chat.common.exception.ChatException;
import com.shurona.chat.mytalk.chat.domain.model.ChatLog;
import com.shurona.chat.mytalk.chat.domain.model.ChatRoom;
import com.shurona.chat.mytalk.chat.presentation.dto.endpoint.ReadNotificationDto;
import com.shurona.chat.mytalk.chat.presentation.dto.request.ChatMessageRequestDto;
import com.shurona.chat.mytalk.chat.presentation.dto.request.ChatRoomCreateRequestDto;
import com.shurona.chat.mytalk.chat.presentation.dto.response.ChatLogResponseDto;
import com.shurona.chat.mytalk.chat.presentation.dto.response.ChatRoomResponseDto;
import com.shurona.chat.mytalk.common.response.ApiResponse;
import com.shurona.chat.mytalk.common.response.PageResponse;
import com.shurona.chat.mytalk.common.security.UserDetailsImpl;
import com.shurona.chat.mytalk.user.application.UserService;
import com.shurona.chat.mytalk.user.domain.model.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/chats")
@RestController
public class ChatRestController {

    private static String chatRoomDestinationPrefix = "/topic/room/";

    // Service
    private final ChatService chatService;
    private final UserService userService;

    // chat 구독 유저들에게 전달
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/rooms/private")
    public ApiResponse<ChatRoomResponseDto> createPrivateChatRoom(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestBody ChatRoomCreateRequestDto requestDto
    ) {

        // 채팅방을 생성할 유저
        User userInfo = userService.findUserById(userDetails.userId());

        // 초대 된 유저 목록 조회
        List<User> invitedUserList = userService.findUserList(requestDto.friendUserIds());

        // 채팅방 생성
        ChatRoom chatRoom = chatService.createChatRoom(
            userInfo, invitedUserList, requestDto.roomType(), requestDto.name());

        return ApiResponse.success(
            ChatRoomResponseDto.of(chatRoom)
        );
    }

    @PostMapping("/rooms/{roomId}/messages")
    public ApiResponse<Long> writeChatMessage(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable("roomId") Long roomId,
        @RequestBody ChatMessageRequestDto requestDto
    ) {
        if (!requestDto.chatRoomId().equals(roomId)) {
            throw new ChatException(ChatErrorCode.BAD_REQUEST);
        }

        // 유저와 채팅 정보 조회
        User userInfo = userService.findUserById(userDetails.userId());
        ChatRoom room = chatService.findChatRoomById(roomId);

        ChatLog chatLog = chatService.writeChat(
            room, userInfo, requestDto.message(), requestDto.type());

        // 해당 채팅룸으로 구독한 유저들에게 전달을 해준다..
        messagingTemplate.convertAndSend(
            chatRoomDestinationPrefix + roomId, ChatLogResponseDto.of(chatLog, 0));

        return ApiResponse.success(chatLog.getId());
    }

    @GetMapping("/rooms")
    public ApiResponse<List<ChatRoomResponseDto>> getChatroomList(
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {

        User userInfo = userService.findUserById(userDetails.userId());

        List<ChatRoom> chatRoomListByUser = chatService.findChatRoomListByUser(userInfo);

        return ApiResponse.success(
            chatRoomListByUser.stream().map(
                ChatRoomResponseDto::of
            ).toList()
        );
    }

    /**
     * 채팅 로그를 갖고 온다.
     */
    @GetMapping("/rooms/{roomId}/logs")
    public ApiResponse<PageResponse<ChatLogResponseDto>> getChatLogList(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestParam(name = "page", defaultValue = "0") Integer page,
        @PathVariable("roomId") Long roomId
    ) {

        // 유저와 채팅 정보 조회
        User userInfo = userService.findUserById(userDetails.userId());
        ChatRoom room = chatService.findChatRoomById(roomId);

        // 채팅은 최근 내역을 먼저 불러온다.
        Sort sortInfo = Sort.by(Order.desc("createdAt"));
        Page<ChatLog> logs = chatService.readChatLog(userInfo, room,
            PageRequest.of(0, 100, sortInfo));

        // 채팅 로그 읽지 않은 유저 숫자를 저장 한다.
        Map<Long, Integer> unreadCountByChatRoomId = chatService.findUnreadCountByChatRoomId(room,
            logs.toList());
        List<ChatLogResponseDto> chatLogResponseDtos = logs.stream().map((log) -> {
            int unreadCount = unreadCountByChatRoomId.get(log.getId());
            return ChatLogResponseDto.of(log, unreadCount);
        }).toList();

        // 읽었음 표시해준다. 여기서는 추가되는 Message가 없으므로 null로 전송한다.
        messagingTemplate.convertAndSend(
            chatRoomDestinationPrefix + roomId + "/read-notifications", new ReadNotificationDto(
                userInfo.getId(), LocalDateTime.now()));

        // Front에서는 반대로 표시되어야 하므로 반환해준다.
        return ApiResponse.success(PageResponse.from(logs, chatLogResponseDtos.reversed()));
    }

}
