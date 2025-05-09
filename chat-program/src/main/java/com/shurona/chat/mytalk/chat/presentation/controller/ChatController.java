package com.shurona.chat.mytalk.chat.presentation.controller;

import static com.shurona.chat.mytalk.common.variable.StaticVariable.LOGIN_USER;

import com.shurona.chat.mytalk.chat.application.ChatService;
import com.shurona.chat.mytalk.chat.domain.model.ChatLog;
import com.shurona.chat.mytalk.chat.domain.model.ChatRoom;
import com.shurona.chat.mytalk.chat.domain.type.ChatContentType;
import com.shurona.chat.mytalk.chat.domain.type.RoomType;
import com.shurona.chat.mytalk.chat.presentation.dtos.ChatLogResponseDto;
import com.shurona.chat.mytalk.chat.presentation.dtos.ChatMessageHeaderResponseDto;
import com.shurona.chat.mytalk.chat.presentation.dtos.ChatMessageRequestData;
import com.shurona.chat.mytalk.chat.presentation.dtos.ChatMessageResponseDto;
import com.shurona.chat.mytalk.chat.presentation.dtos.ChatRoomResponseDto;
import com.shurona.chat.mytalk.common.session.UserSession;
import com.shurona.chat.mytalk.friend.application.FriendService;
import com.shurona.chat.mytalk.friend.domain.model.Friend;
import com.shurona.chat.mytalk.user.domain.model.User;
import com.shurona.chat.mytalk.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@RequestMapping("/chats/v1")
@Controller
public class ChatController {

    // Service
    private final ChatService chatService;
    private final UserService userService;
    private final FriendService friendService;

    // chat 전달 용
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/rooms")
    public String chatListPage(
        Model model,
        HttpSession session
    ) {

        UserSession currentUser = (UserSession) session.getAttribute(LOGIN_USER);
        User userInfo = userService.findUserById(currentUser.userId());

        List<ChatRoom> chatRoomListByUser = chatService.findChatRoomListByUser(userInfo);

        model.addAttribute("chatRooms",
            chatRoomListByUser.stream().map(ChatRoomResponseDto::of).toList());
        model.addAttribute("userInfo", userInfo);

        return "chat/home";
    }

    @PostMapping("/rooms/private")
    public String createChatRoom(
        @RequestParam("requestId") Long requestId,
        HttpSession session
    ) {

        UserSession currentUser = (UserSession) session.getAttribute(LOGIN_USER);
        User userInfo = userService.findUserById(currentUser.userId());
        Friend friend = friendService.findFriendById(requestId);

        // 조회 해서 이미 확인하는지 확인하고 이미 있으면 채팅방으로 redirect

        if (true) {
            Long chatRoomId = 1L;
            return "redirect:/chats/v1/rooms/" + chatRoomId;
        }

        chatService.createChatRoom(userInfo, List.of(friend.getFriend()), RoomType.PRIVATE, "개인톡");

        return "redirect:/chats/v1/rooms";
    }

    @GetMapping("/rooms/{id}")
    public String singleChatroom(
        @PathVariable("id") Long roomId,
        Model model,
        HttpSession session
    ) {
        UserSession currentUser = (UserSession) session.getAttribute(LOGIN_USER);
        User userInfo = userService.findUserById(currentUser.userId());
        ChatRoom room = chatService.findChatRoomById(roomId);

        // 일단 100 줄만 먼저 읽어온다
        List<ChatLogResponseDto> dtos = chatService.readChatLog(userInfo, room,
            PageRequest.of(0, 100));

        // 메시지 목록
        model.addAttribute("messages",
            dtos.reversed().stream().map(
                dto -> ChatMessageResponseDto.of(dto, userInfo.getLoginId(), userInfo.getId())));

        // 채팅창에서 글로벌로 사용할 것
        model.addAttribute("header", new ChatMessageHeaderResponseDto(
            room.getId(), userInfo.getLoginId(), "partner", room.getName()));

        return "chat/room";
    }

    @PostMapping("/rooms/{id}/message")
    public ResponseEntity<Void> writeMessage(
        HttpSession session,
        @PathVariable("id") Long roomId,
        @RequestBody ChatMessageRequestData data
    ) {
        // 유저 정보를 갖고 온다.
        UserSession currentUser = (UserSession) session.getAttribute(LOGIN_USER);
        User userInfo = userService.findUserById(currentUser.userId());
        ChatRoom room = chatService.findChatRoomById(roomId);

        // 구독된 채팅방을 지칭한다.
        // TODO: 어디다 저장하는 것이 좋은 것일까
        String chatRoomDestination = "/topic/room/" + roomId;

        ChatLog chatLog = chatService.writeChat(
            room, userInfo, data.message(), ChatContentType.TEXT);

        ChatLogResponseDto chatLogResponseDto = ChatLogResponseDto.of(chatLog, 0);

        // 해당 채팅룸으로 구독한 유저들에게 전달을 해준다..
        messagingTemplate.convertAndSend(chatRoomDestination,
            ChatMessageResponseDto.of(chatLogResponseDto,
                userInfo.getLoginId(), currentUser.userId()));

        return ResponseEntity.ok().build();
    }

}
