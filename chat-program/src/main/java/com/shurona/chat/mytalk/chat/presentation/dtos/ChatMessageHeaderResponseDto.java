package com.shurona.chat.mytalk.chat.presentation.dtos;

public record ChatMessageHeaderResponseDto(
    Long id,
    String loginId,
    String partner,
    String name
) {

}
