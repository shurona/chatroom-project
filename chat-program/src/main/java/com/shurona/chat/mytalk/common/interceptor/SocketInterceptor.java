package com.shurona.chat.mytalk.common.interceptor;

import com.shurona.chat.mytalk.chat.common.exception.ChatErrorCode;
import com.shurona.chat.mytalk.chat.common.exception.ChatException;
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

    private final JwtUtils jwtUtils;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        log.info("이건 정말인가요 , 연결 상태 {}", accessor.getCommand());

        // 연결 할 때 인증 정보를 등록해준다.
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            // 토큰 추출
            String token = jwtUtils.substringToken(
                accessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION)
            );

            if (!jwtUtils.checkValidJwtToken(token)) {
                throw new ChatException(ChatErrorCode.PRIVATE_CHAT_JUST_ONE_REQUIRED);
            }

            Claims bodyFromJwt = jwtUtils.getBodyFromJwt(token);

            long userId = Long.parseLong(bodyFromJwt.getSubject()); // 사용자 ID 추출
            String role = bodyFromJwt.get(JwtUtils.AUTHORIZATION_KEY,
                String.class); // 사용자 권한 추출

            accessor.setUser(() -> String.valueOf(userId));
            // 세션 속성에도 저장
            accessor.getSessionAttributes().put("userId", userId);

        } else if (StompCommand.SEND.equals(accessor.getCommand())) {
            // 세션에서 userId 가져와서 Principal 설정
            Object userId = accessor.getSessionAttributes().get("userId");
            if (userId != null) {
                accessor.setUser(() -> String.valueOf(userId));
            } else {
                throw new ChatException(ChatErrorCode.SOCKET_NOT_AUTHORIZATION);
            }
        }

        return message;
    }

}
