package com.shurona.chat.mytalk.chat.presentation.controller;

import static com.shurona.chat.mytalk.common.variable.StaticVariable.LOGIN_USER;

import com.shurona.chat.mytalk.chat.application.ChatService;
import com.shurona.chat.mytalk.chat.domain.model.ChatRoom;
import com.shurona.chat.mytalk.chat.domain.type.RoomType;
import com.shurona.chat.mytalk.chat.presentation.dtos.ChatLogResponseDto;
import com.shurona.chat.mytalk.chat.presentation.dtos.ChatMessageData;
import com.shurona.chat.mytalk.chat.presentation.dtos.ChatMessageHeaderResponseDto;
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

    private final ChatService chatService;
    private final UserService userService;
    private final FriendService friendService;

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

        List<ChatLogResponseDto> dtos = chatService.readChatLog(userInfo, room,
            PageRequest.of(0, 100));

        model.addAttribute("messages",
            dtos.stream().map(
                dto -> ChatMessageResponseDto.of(dto, userInfo.getLoginId(), userInfo.getId())));
        model.addAttribute("header", new ChatMessageHeaderResponseDto(
            room.getId(), "partner", room.getName()));

        return "chat/room";
    }

    @PostMapping("/rooms/{id}/message")
    public ResponseEntity<Void> writeMessage(
        @PathVariable("id") Long roomId,
        @RequestBody ChatMessageData data
    ) {

        System.out.println("아이디 : " + roomId + " : " + data);
        return ResponseEntity.ok().build();
    }

}
