package com.shurona.chat.mytalk.chat.common.config;

import com.shurona.chat.mytalk.chat.infrastructure.redis.subscriber.RedisSubscriber;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@RequiredArgsConstructor
@Configuration
public class RedisPubSubConfig {

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer( // (1)
        RedisConnectionFactory connectionFactory, // Redis 연결을 위한 팩토리
        MessageListenerAdapter listenerAdapter, // 메시지를 받았을 때 실행될 리스너 어댑터
        ChannelTopic chatRoomTopic // 구독할 채널 이름
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listenerAdapter, chatRoomTopic);

        return container;
    }

    @Bean
    public MessageListenerAdapter listenerAdapter(RedisSubscriber subscriber) {
        return new MessageListenerAdapter(subscriber, "onMessage");
    }


    // 채팅 에서 사용할 구독 topic
    @Bean
    public ChannelTopic chatRoomTopic() {
        return new ChannelTopic("chatroom");
    }

    @Bean
    public ChannelTopic noticeTopic() {
        return new ChannelTopic("notice");
    }


}
