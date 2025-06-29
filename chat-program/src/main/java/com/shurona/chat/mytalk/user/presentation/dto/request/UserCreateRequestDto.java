package com.shurona.chat.mytalk.user.presentation.dto.request;

public record UserCreateRequestDto(
    String loginId,
    String username, // 유저 닉네임 일단 지금은 사용하지 않음.
    String password,
    String description,
    String phoneNumber
) {

}
