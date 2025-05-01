package com.shurona.chat.mytalk.chat.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ChatErrorCode {

    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 입력값입니다."),
    USER_NOT_INCLUDE_ROOM(HttpStatus.BAD_REQUEST, "유저가 채팅방에 속해있지 않습니다."),
    PRIVATE_CHATROOM_ONLY_ONE(HttpStatus.BAD_REQUEST, "개인톡은 하나만 존재해야 한다."),
    PRIVATE_CHAT_JUST_ONE_REQUIRED(HttpStatus.BAD_REQUEST, "개인톡은 한 명만 만들 수 있습니다."),
    PRIVATE_CHAT_FRIEND_REQUIRED(HttpStatus.BAD_REQUEST, "개인톡은 친구만 만들 수 있습니다.");

    private HttpStatus status;
    private String message;

}
