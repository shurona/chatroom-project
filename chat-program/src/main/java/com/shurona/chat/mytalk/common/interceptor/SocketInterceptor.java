package com.shurona.chat.mytalk.common.interceptor;

import com.shurona.chat.mytalk.chat.common.exception.ChatErrorCode;
import com.shurona.chat.mytalk.chat.common.exception.ChatException;
import com.shurona.chat.mytalk.user.common.exception.UserErrorCode;
import com.shurona.chat.mytalk.user.common.exception.UserException;
import com.shurona.chat.mytalk.user.common.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class SocketInterceptor implements ChannelInterceptor {

    private static final String USER_ID_KEY = "userId";
    private final JwtUtils jwtUtils;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();

        System.out.println(accessor);

        if (StompCommand.CONNECT.equals(command)) {
            handleConnect(accessor);
        } else if (StompCommand.SEND.equals(command)) {
            handleSend(accessor);
        } else if (StompCommand.DISCONNECT.equals(command)) {
            handleDisconnect(accessor);
        }

        return message;
    }

    private void handleConnect(StompHeaderAccessor accessor) {
        log.info("연결되었습니다. , 연결 상태 {}", accessor.getCommand());

        String token = extractToken(accessor);
        validateToken(token);

        Claims claims = jwtUtils.getBodyFromJwt(token);
        long userId = extractUserId(claims);

        setUserAuthentication(accessor, userId);
    }

    private String extractToken(StompHeaderAccessor accessor) {
        try {
            return jwtUtils.substringToken(
                accessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION)
            );
        } catch (Exception e) {
            throw new UserException(UserErrorCode.JWT_TOKEN_INVALID);
        }
    }

    // token의 validation을 처리한다.
    private void validateToken(String token) {
        if (!jwtUtils.checkValidJwtToken(token)) {
            throw new ChatException(ChatErrorCode.INVALID_JWT_TOKEN);
        }
    }

    // JwtClaim에서 UserId를 추출한다.
    private long extractUserId(Claims claims) {
        return Long.parseLong(claims.getSubject());
    }

    //
    private void setUserAuthentication(StompHeaderAccessor accessor, long userId) {
        accessor.setUser(() -> String.valueOf(userId));
        accessor.getSessionAttributes().put(USER_ID_KEY, userId);
    }

    private void handleSend(StompHeaderAccessor accessor) {
        Object userId = accessor.getSessionAttributes().get(USER_ID_KEY);
        if (userId != null) {
            accessor.setUser(() -> String.valueOf(userId));
        } else {
            throw new ChatException(ChatErrorCode.SOCKET_NOT_AUTHORIZATION);
        }
    }

    private void handleDisconnect(StompHeaderAccessor accessor) {
        log.info("연결 해제되었습니다. , 연결 상태 {}", accessor.getCommand());
    }
}
