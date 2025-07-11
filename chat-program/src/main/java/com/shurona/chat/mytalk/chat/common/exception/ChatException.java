package com.shurona.chat.mytalk.chat.common.exception;

import lombok.Getter;

@Getter
public class ChatException extends RuntimeException {

    private final ChatErrorCode code;

    public ChatException(ChatErrorCode code) {
        super(code.getMessage());
        this.code = code;
    }
}
