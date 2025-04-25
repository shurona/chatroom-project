package com.shurona.chat.mytalk.friend.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum FriendErrorCode {

    INVALID_INPUT(HttpStatus.BAD_REQUEST, "잘못된 입력입니다."),

    FRIEND_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "이미 친구가 존재합니다.");

    private HttpStatus status;
    private String message;

    FriendErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
