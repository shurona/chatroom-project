package com.shurona.chat.mytalk.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.shurona.chat.mytalk.chat.application.cache.dtos.LastMessageOfChatDto;
import com.shurona.chat.mytalk.common.variable.RedisProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@RequiredArgsConstructor
@EnableCaching
@Configuration
public class RedisConfig {

    private final RedisProperty redisProperty;

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Java 8 시간 타입 지원
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new Jdk8Module());

        // 날짜를 ISO-8601 문자열로 저장
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // 알 수 없는 속성 무시
//        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return mapper;
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();

        config.setHostName(redisProperty.host());
        config.setUsername(redisProperty.username());
        config.setPort(redisProperty.port());
        config.setPassword(redisProperty.password());

        return new LettuceConnectionFactory(config);
    }

    /**
     * RedisTemplate을 생성하여 Redis 연결 및 직렬화 방식을 설정합니다.
     * Key는 String으로, Value는 Jackson을 이용한 JSON(String.class)으로 직렬화합니다.
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {

        Jackson2JsonRedisSerializer<Object> jsonSerializer =
            new Jackson2JsonRedisSerializer<>(objectMapper(), Object.class);

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(jsonSerializer);
        return redisTemplate;
    }

    /**
     * LastMessageOfChatDto Redis Template
     * 채팅의 마지막 메시지와 기록을 확인하기 위한 Redis
     */
    @Bean
    public RedisTemplate<String, LastMessageOfChatDto> chatRoomLastTimeTemplate(
        RedisConnectionFactory connectionFactory) {

        // Jackson2JsonRedisSerializer 사용
        Jackson2JsonRedisSerializer<LastMessageOfChatDto> serializer = new Jackson2JsonRedisSerializer<>(
            objectMapper(), LastMessageOfChatDto.class);
        RedisTemplate<String, LastMessageOfChatDto> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(serializer);

        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(serializer);

        return redisTemplate;
    }

}
