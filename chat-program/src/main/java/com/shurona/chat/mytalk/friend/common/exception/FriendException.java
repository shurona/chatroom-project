package com.shurona.chat.mytalk.friend.common.exception;

import org.springframework.http.HttpStatus;

public class FriendException extends RuntimeException{
    private final HttpStatus httpStatus;

    public FriendException(HttpStatus status, String message) {
        super(message);
        httpStatus = status;
    }

}
