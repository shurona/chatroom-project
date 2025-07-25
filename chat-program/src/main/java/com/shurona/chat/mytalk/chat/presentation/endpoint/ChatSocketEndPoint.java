package com.shurona.chat.mytalk.chat.presentation.endpoint;

import com.shurona.chat.mytalk.chat.application.ChatService;
import com.shurona.chat.mytalk.chat.domain.model.ChatLog;
import com.shurona.chat.mytalk.chat.domain.model.ChatRoom;
import com.shurona.chat.mytalk.chat.presentation.dto.endpoint.ReadRequest;
import com.shurona.chat.mytalk.chat.presentation.dto.endpoint.UnReadUserResponseDto;
import com.shurona.chat.mytalk.user.application.UserService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ChatSocketEndPoint {

    // Service
    private final ChatService chatService;
    private final UserService userService;

    // chat 구독 유저들에게 전달
    private final SimpMessagingTemplate messagingTemplate;

    // 읽음 처리 요청을 받았을 때
    @MessageMapping("/room/{roomId}/read")
    public void handleRead(
        @DestinationVariable Long roomId,
        @Payload ReadRequest request,
        SimpMessageHeaderAccessor accessor,
        @Header("reply-to") String replyTo
    ) {

        Object userObjectId = accessor.getSessionAttributes().get("userId");
        if (userObjectId == null) {
            log.error("User ID not found in session attributes.");
            return;
        }

        // 채팅의 UnreadCount를 조회 한다.
        ChatRoom room = chatService.findChatRoomById(roomId);
        List<ChatLog> chatLogList = chatService.findChatLogByIds(request.messageIds());
        Map<Long, Integer> unreadCountByChatRoomId = chatService.findUnreadCountByChatRoomId(
            room, chatLogList);

        messagingTemplate.convertAndSend(
            replyTo,
            new UnReadUserResponseDto(unreadCountByChatRoomId)
        );

    }
}
