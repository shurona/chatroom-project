package com.shurona.chat.mytalk.chat.presentation.dto;

import com.shurona.chat.mytalk.chat.domain.model.ChatLog;
import com.shurona.chat.mytalk.chat.domain.type.ChatContentType;
import com.shurona.chat.mytalk.user.domain.model.User;
import java.time.LocalDateTime;

public record ChatLogSsrResponseDto(
    Long id,
    String content,
    ChatContentType type,
    User writer,
    LocalDateTime wroteTime,
    int unreadCount
) {

    public static ChatLogSsrResponseDto of(ChatLog log, int unreadCount) {
        return new ChatLogSsrResponseDto(
            log.getId(), log.getContent(), log.getType(),
            log.getUser(), log.getCreatedAt(), unreadCount);
    }

}
