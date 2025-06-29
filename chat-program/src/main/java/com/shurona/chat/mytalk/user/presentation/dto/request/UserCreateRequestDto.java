package com.shurona.chat.mytalk.user.presentation.dto.request;

public record UserCreateRequestDto(
    String loginId,
    String nickName,
    String password,
    String description,
    String phoneNumber
) {

}
