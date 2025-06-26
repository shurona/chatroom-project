package com.shurona.chat.mytalk.common.dto;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
public record CommonResponseDto<T>(
    boolean success,

    @JsonInclude(value = NON_NULL)
    String message,

    @JsonInclude(value = NON_NULL)
    T data
) {

    public static <T> CommonResponseDto<T> of(String message, T data) {
        return CommonResponseDto.<T>builder()
            .success(true)
            .message(message)
            .data(data)
            .build();
    }

    public static <T> CommonResponseDto<T> ofData(T data) {
        return CommonResponseDto.<T>builder()
            .success(true)
            .data(data)
            .build();
    }
}
