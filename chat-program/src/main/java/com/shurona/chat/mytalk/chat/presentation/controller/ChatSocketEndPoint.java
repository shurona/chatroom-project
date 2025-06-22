package com.shurona.chat.mytalk.chat.presentation.controller;

import com.shurona.chat.mytalk.chat.application.ChatService;
import com.shurona.chat.mytalk.chat.presentation.dtos.ChatRecentReadWithSubRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ChatSocketEndPoint {

    private final ChatService chatService;

    @MessageMapping("/chats/v1/socket/room")
    public void readSetting(
        ChatRecentReadWithSubRequestDto requestDto,
        SimpMessageHeaderAccessor accessor) {

        System.out.println("들어와서 로직 처리");
    }
}
