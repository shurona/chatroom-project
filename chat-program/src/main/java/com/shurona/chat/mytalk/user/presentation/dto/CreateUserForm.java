package com.shurona.chat.mytalk.user.presentation.dto;

public record CreateUserForm(
    String loginId,
    String password,
    String description,
    String phoneNumber
) {

}
