package com.shurona.chat.mytalk.chat.presentation.dto.response;

import com.shurona.chat.mytalk.chat.domain.model.ChatLog;
import com.shurona.chat.mytalk.chat.domain.type.ChatContentType;
import java.time.LocalDateTime;

public record ChatLogResponseDto(
    Long id,
    String content,
    ChatContentType type,
    Long writerId,
    String writeUserNickName,
    LocalDateTime wroteTime,
    int unreadCount
) {

    public static ChatLogResponseDto of(ChatLog log, int unreadCount) {
        return new ChatLogResponseDto(
            log.getId(), log.getContent(), log.getType(),
            log.getUser().getId(), log.getUser().getNickName(),
            log.getCreatedAt(), unreadCount);
    }

}
