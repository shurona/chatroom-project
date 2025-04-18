package com.shurona.chat.mytalk.presentation.dto;

public record CreateUserForm(
    String loginId,
    String password,
    String description,
    String phoneNumber
) {

}
