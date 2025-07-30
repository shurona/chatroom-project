package com.shurona.chat.mytalk.common.interceptor;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.shurona.chat.mytalk.chat.common.exception.ChatException;
import com.shurona.chat.mytalk.user.common.exception.UserErrorCode;
import com.shurona.chat.mytalk.user.common.exception.UserException;
import com.shurona.chat.mytalk.user.common.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import java.util.HashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;

@ExtendWith(MockitoExtension.class)
class SocketInterceptorTest {

    @Mock
    private JwtUtils jwtUtils;

    private SocketInterceptor socketInterceptor;

    @BeforeEach
    void setUp() {
        socketInterceptor = new SocketInterceptor(jwtUtils);
    }

    @Test
    void testHandleConnect_ValidToken() {
        // Given
        String validToken = "valid.jwt.token";
        Claims claims = mock(Claims.class);
        when(jwtUtils.substringToken(anyString())).thenReturn(validToken);
        when(jwtUtils.checkValidJwtToken(validToken)).thenReturn(true);
        when(jwtUtils.getBodyFromJwt(validToken)).thenReturn(claims);
        when(claims.getSubject()).thenReturn("123");

        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        accessor.setNativeHeader(HttpHeaders.AUTHORIZATION, "Bearer " + validToken);
        accessor.setSessionAttributes(new HashMap<>());
        Message<?> message = MessageBuilder.createMessage(new byte[0],
            accessor.getMessageHeaders());

        // When
        socketInterceptor.preSend(message, mock(MessageChannel.class));

        // Then
        verify(jwtUtils).checkValidJwtToken(validToken);
        verify(jwtUtils).getBodyFromJwt(validToken);
    }

    @Test
    void testHandleConnect_InvalidToken() {
        // Given
        when(jwtUtils.substringToken(anyString())).thenThrow(
            new UserException(UserErrorCode.JWT_TOKEN_INVALID));

        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        accessor.setNativeHeader(HttpHeaders.AUTHORIZATION, "Bearer invalid.token");
        Message<?> message = MessageBuilder.createMessage(new byte[0],
            accessor.getMessageHeaders());

        // When & Then
        assertThrows(UserException.class,
            () -> socketInterceptor.preSend(message, mock(MessageChannel.class)));
    }

    @Test
    void testHandleSend_Unauthorized() {
        // Given
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SEND);
        accessor.setSessionAttributes(new HashMap<>());
        Message<?> message = MessageBuilder.createMessage(new byte[0],
            accessor.getMessageHeaders());

        // When & Then
        assertThrows(ChatException.class,
            () -> socketInterceptor.preSend(message, mock(MessageChannel.class)));
    }

}