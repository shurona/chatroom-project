package com.shurona.chat.mytalk.common.config;

import com.shurona.chat.mytalk.common.variable.RedisProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

@RequiredArgsConstructor
@EnableCaching
@Configuration
public class RedisConfig {

    private final RedisProperty redisProperty;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        System.out.println("redisProperty = " + redisProperty);

        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();

        config.setHostName(redisProperty.host());
        config.setUsername(redisProperty.username());
        config.setPort(redisProperty.port());
        config.setPassword(redisProperty.password());

        return new LettuceConnectionFactory(config);
    }

    //    @Bean
    public RedisTemplate<String, String> stringRedisTemplate() {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(redisConnectionFactory());

        // Serializer 설정
        redisTemplate.setKeySerializer(RedisSerializer.string());
        redisTemplate.setValueSerializer(RedisSerializer.string());

        // Hash Serializer 설정
        redisTemplate.setHashKeySerializer(RedisSerializer.string());
        redisTemplate.setHashValueSerializer(RedisSerializer.string());

        return redisTemplate;
    }

}
