package com.shurona.chat.mytalk.chat.presentation.dto.request;

import com.shurona.chat.mytalk.chat.domain.type.RoomType;
import java.util.List;

public record ChatRoomCreateRequestDto(
    List<Long> friendUserIds,
    String name,
    RoomType roomType
) {

}
