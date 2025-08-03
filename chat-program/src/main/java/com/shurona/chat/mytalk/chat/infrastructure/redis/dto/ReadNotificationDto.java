package com.shurona.chat.mytalk.chat.infrastructure.redis.dto;

import java.time.LocalDateTime;

public record ReadNotificationDto(
    Long userId,
    LocalDateTime readAt
) {

}
