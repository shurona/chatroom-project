package com.shurona.chat.mytalk.common.config;

import com.shurona.chat.mytalk.common.handler.CustomStompErrorHandler;
import com.shurona.chat.mytalk.common.interceptor.SocketInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class SocketConfig implements WebSocketMessageBrokerConfigurer {

    // interceptor
    private final SocketInterceptor socketInterceptor;

    // error handler
    private final CustomStompErrorHandler customStompErrorHandler;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("socket/chat")
            .setAllowedOrigins("http://localhost:3000")
            .withSockJS();

        registry.setErrorHandler(customStompErrorHandler);
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(socketInterceptor);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 클라이언트가 서버로 메시지를 보낼 때 사용할 prefix 설정
        registry.setApplicationDestinationPrefixes("/app");
        // 서버가 클라이언트에게 메시지를 전달할 때 사용할 topic, queue 브로커 활성화
        registry.enableSimpleBroker("/topic", "/queue");
    }
}
