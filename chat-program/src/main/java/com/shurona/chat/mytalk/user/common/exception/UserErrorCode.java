package com.shurona.chat.mytalk.user.common.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum UserErrorCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    // jwt 관련
    JWT_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "JWT 토큰이 없습니다."),
    JWT_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "JWT 토큰이 만료되었습니다."),
    JWT_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "JWT 토큰이 유효하지 않습니다."),
    ;

    private HttpStatus status;
    private String message;
}
