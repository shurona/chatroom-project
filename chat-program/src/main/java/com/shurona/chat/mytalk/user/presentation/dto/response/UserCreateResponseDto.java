package com.shurona.chat.mytalk.user.presentation.dto.response;

public record UserCreateResponseDto(
    Long userId,
    String loginId
) {

    public static UserCreateResponseDto of(Long userId, String loginIn) {
        return new UserCreateResponseDto(userId, loginIn);
    }
}
