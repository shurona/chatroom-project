package com.shurona.chat.mytalk.friend.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class FriendException extends RuntimeException{
    private final FriendErrorCode code;

    public FriendException(FriendErrorCode code) {
        super(code.getMessage());
        this.code = code;
    }

}
