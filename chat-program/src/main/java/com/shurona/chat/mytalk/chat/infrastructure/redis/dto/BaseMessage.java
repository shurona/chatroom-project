package com.shurona.chat.mytalk.chat.infrastructure.redis.dto;

public record BaseMessage<T>(
    MessageType type,
    Long roomId,
    T payload // 실제 데이터
) {

    public static <T> BaseMessage<T> from(MessageType type, Long roomId, T payload) {
        return new BaseMessage<>(
            type, roomId, payload
        );
    }
}
