package com.shurona.chat.mytalk.chat.application.cache.dtos;

import java.time.LocalDateTime;

public record LastMessageOfChatDto(
    String content,
    LocalDateTime lastTime
) {

}
