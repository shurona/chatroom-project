package com.shurona.chat.mytalk.chat.presentation.dtos;

import com.shurona.chat.mytalk.chat.domain.model.ChatRoom;
import java.time.LocalDateTime;

public record ChatRoomResponseDto(
    Long id,
    String name,
    String lastMessage,
    int unreadCount,
    LocalDateTime lastMessageTime

) {

    public static ChatRoomResponseDto of(
        ChatRoom room) {
        return new ChatRoomResponseDto(
            room.getId(),
            room.getName(),
            room.getLastMessage(),
            0,
            room.getLastTime()
        );
    }

}
