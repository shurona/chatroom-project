package com.shurona.chat.mytalk.user.presentation.dto.response;

import com.shurona.chat.mytalk.user.domain.model.User;

public record UserResponseDto(
    Long userId,
    String loginId,
    String nickName,
    String description,
    String phoneNumber
) {

    public static UserResponseDto from(User user) {
        return new UserResponseDto(
            user.getId(),
            user.getLoginId(),
            user.getNickName(),
            user.getDescription(),
            user.getPhoneNumber().getPhoneNumber()
        );
    }

}
