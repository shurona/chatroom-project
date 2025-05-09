package com.shurona.chat.mytalk.chat.presentation.dtos;

import com.shurona.chat.mytalk.chat.domain.type.ChatContentType;
import java.time.LocalDateTime;

public record ChatMessageResponseDto(
    Long id,
    String loginId,
    ChatContentType type,
    String content,
    int unreadCount,
    boolean mine,
    LocalDateTime wroteTime
) {

    public static ChatMessageResponseDto of(
        ChatLogResponseDto responseDto, String loginId, Long userId) {

        return new ChatMessageResponseDto(responseDto.id(), responseDto.writer().getLoginId(),
            responseDto.type(), responseDto.content(), responseDto.unreadCount(),
            responseDto.writer().getId() == userId, responseDto.wroteTime());
    }

}
