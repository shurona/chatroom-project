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

        log.error("클라이언트 메시지 처리 중 에러 발생: {}", ex.getMessage(), ex);

        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
        accessor.setMessage("요청 처리 중 오류가 발생했습니다.");
        accessor.setLeaveMutable(true);

        // 보안을 위해 내부 에러 메시지는 숨기고 사용자 친화적 메시지 제공
        switch (ex) {
            case ChatException chatException -> accessor.setMessage("인증이 필요합니다.");
            case UserException userException -> {
                accessor.setMessage("접근 권한이 없습니다.");
                System.out.println("여기는 들어 오나요?");
            }
            case IllegalArgumentException illegalArgumentException ->
                accessor.setMessage("잘못된 요청입니다.");
            default -> {
                System.out.println("아예 예외 입니다.");
            }
        }

        // custom Error Handling
        return handleInternal(accessor, EMPTY_PAYLOAD, ex, null);
    }


    @Override
    public Message<byte[]> handleErrorMessageToClient(Message<byte[]> errorMessage) {
        log.error("서버에서 클라이언트로 에러 메시지 전송: {}", errorMessage);

        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(errorMessage,
            StompHeaderAccessor.class);
        Assert.notNull(accessor, "No StompHeaderAccessor");
        // 원본 에러 메시지를 그대로 전달
        return errorMessage;
    }
}
