package com.shurona.chat.mytalk.chat.infrastructure.redis.dto;

import com.shurona.chat.mytalk.chat.domain.model.ChatLog;
import com.shurona.chat.mytalk.chat.domain.type.ChatContentType;
import java.time.LocalDateTime;

public record ChatMessageDto(
    Long id,
    String content,
    ChatContentType type,
    Long writerId,
    String writeUserNickName,
    LocalDateTime wroteTime,
    int unreadCount
) {

    public static ChatMessageDto of(ChatLog log, int unreadCount) {
        return new ChatMessageDto(
            log.getId(), log.getContent(), log.getType(),
            log.getUser().getId(), log.getUser().getNickName(),
            log.getCreatedAt(), unreadCount);
    }
}
