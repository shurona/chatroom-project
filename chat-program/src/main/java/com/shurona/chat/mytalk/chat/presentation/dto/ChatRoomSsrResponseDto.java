package com.shurona.chat.mytalk.chat.presentation.dto;

import com.shurona.chat.mytalk.chat.domain.model.ChatRoom;
import java.time.LocalDateTime;

public record ChatRoomSsrResponseDto(
    Long id,
    String name,
    String lastMessage,
    int unreadCount,
    LocalDateTime lastMessageTime

) {

    public static ChatRoomSsrResponseDto of(
        ChatRoom room) {
        return new ChatRoomSsrResponseDto(
            room.getId(),
            room.getName(),
            room.getLastMessage(),
            0,
            room.getLastTime()
        );
    }

}
