package com.shurona.chat.mytalk.user.common.exception;

import lombok.Getter;

@Getter
public class UserException extends RuntimeException {

    private final UserErrorCode code;

    public UserException(UserErrorCode code) {
        super(code.getMessage());
        this.code = code;
    }

}
