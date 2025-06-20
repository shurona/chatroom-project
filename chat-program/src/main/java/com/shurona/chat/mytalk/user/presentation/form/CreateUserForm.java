package com.shurona.chat.mytalk.user.presentation.form;

public record CreateUserForm(
    String loginId,
    String password,
    String description,
    String phoneNumber
) {

}
