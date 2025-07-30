package com.shurona.chat.mytalk.common.handler;

import com.shurona.chat.mytalk.chat.common.exception.ChatException;
import com.shurona.chat.mytalk.user.common.exception.UserException;
import io.jsonwebtoken.lang.Assert;
import io.micrometer.common.lang.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

@Slf4j
@Component
public class CustomStompErrorHandler extends StompSubProtocolErrorHandler {

    private static final byte[] EMPTY_PAYLOAD = new byte[0];

    @Override
    public Message<byte[]> handleClientMessageProcessingError(
        @Nullable Message<byte[]> clientMessage, Throwable ex) {

        // ERROR 프레임을 위한 StompHeaderAccessor 생성
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);

        // 예외의 root cause를 추적
        Throwable rootCause = getRootCause(ex);

        // 보안을 위해 내부 에러 메시지는 숨기고 사용자 친화적 메시지 제공
        // 예외 메시지 매핑
        String userFriendlyMessage = mapExceptionToMessage(rootCause);
        accessor.setMessage(userFriendlyMessage);

        // 헤더를 수정 가능하게 설정
        accessor.setLeaveMutable(true);

        // custom Error Handling
        return handleInternal(accessor, EMPTY_PAYLOAD, ex, null);
    }


    // TODO: 이 메소드는 언제 업데이트 해볼 수 있으려나
    @Override
    public Message<byte[]> handleErrorMessageToClient(Message<byte[]> errorMessage) {
        log.error("서버에서 클라이언트로 에러 메시지 전송: {}", errorMessage);

        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(errorMessage,
            StompHeaderAccessor.class);
        Assert.notNull(accessor, "No StompHeaderAccessor");
        // 원본 에러 메시지를 그대로 전달
        return errorMessage;
    }

    // 예외의 root cause를 추적하는 메서드
    private Throwable getRootCause(Throwable ex) {
        Throwable rootCause = ex;
        while (rootCause.getCause() != null) {
            rootCause = rootCause.getCause();
        }
        return rootCause;
    }

    // 예외를 사용자 친화적 메시지로 매핑하는 메서드
    private String mapExceptionToMessage(Throwable rootCause) {
        if (rootCause instanceof ChatException) {
            return "채팅 관련 오류 입니다.";
        } else if (rootCause instanceof UserException) {
            return "접근 권한이 없습니다.";
        } else if (rootCause instanceof IllegalArgumentException) {
            return "잘못된 요청입니다.";
        } else {
            return "요청 처리 중에 알 수 없는 오류가 발생했습니다.";
        }
    }
}
