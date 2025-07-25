package com.shurona.chat.mytalk.chat.presentation.dto.endpoint;

import java.time.LocalDateTime;

public record ReadNotificationDto(
    Long userId,
    LocalDateTime readAt
) {

}
