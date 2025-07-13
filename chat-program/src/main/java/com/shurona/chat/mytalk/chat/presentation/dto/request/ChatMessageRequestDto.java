package com.shurona.chat.mytalk.chat.presentation.dto.request;

import com.shurona.chat.mytalk.chat.domain.type.ChatContentType;

public record ChatMessageRequestDto(
    Long chatRoomId,
    String message,
    ChatContentType type
) {

}
