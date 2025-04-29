package com.shurona.chat.mytalk.chat.common;

import lombok.Getter;

@Getter
public class ChatException extends RuntimeException{

    private final ChatErrorCode code;

    public ChatException(ChatErrorCode code) {
        super(code.getMessage());
        this.code = code;
    }
}
