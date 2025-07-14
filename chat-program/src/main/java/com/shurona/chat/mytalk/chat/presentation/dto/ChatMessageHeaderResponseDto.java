package com.shurona.chat.mytalk.chat.presentation.dto;

public record ChatMessageHeaderResponseDto(
    Long id,
    String loginId,
    String partner,
    String name
) {

}
