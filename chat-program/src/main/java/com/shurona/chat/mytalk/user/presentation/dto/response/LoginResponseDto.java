package com.shurona.chat.mytalk.user.presentation.dto.response;

public record LoginResponseDto(
    String accessToken,
    String refreshToken
) {

}
