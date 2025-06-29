package com.shurona.chat.mytalk.user.presentation.dto.response;

public record UserCreateResponseDto(
    Long userId,
    String loginId
) {

    public static UserCreateResponseDto of(Long userId, String loginId) {
        return new UserCreateResponseDto(userId, loginId);
    }
}
