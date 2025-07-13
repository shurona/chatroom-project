package com.shurona.chat.mytalk.chat.presentation.dto.response;

import com.shurona.chat.mytalk.chat.domain.model.ChatRoom;
import com.shurona.chat.mytalk.chat.domain.type.RoomType;
import java.time.LocalDateTime;

public record ChatRoomResponseDto(
    Long id,
    String name,
    RoomType roomType,
    String lastMessage,
    LocalDateTime lastTime,
    Integer memberCt,
    Integer unreadCt,
    LocalDateTime createdAt
) {

    public static ChatRoomResponseDto of(ChatRoom chatRoom) {

        return new ChatRoomResponseDto(
            chatRoom.getId(),
            chatRoom.getName(),
            chatRoom.getType(),
            chatRoom.getLastMessage(),
            chatRoom.getLastTime(),
            0,
            0,
            chatRoom.getCreatedAt());
    }

}
