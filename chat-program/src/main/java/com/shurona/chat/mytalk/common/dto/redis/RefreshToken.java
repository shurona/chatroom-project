package com.shurona.chat.mytalk.common.dto.redis;

import static com.shurona.chat.mytalk.user.common.utils.JwtUtils.REFRESH_TOKEN_TIME;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@RedisHash(value = "token", timeToLive = (REFRESH_TOKEN_TIME / 1000))
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class RefreshToken {

    @Id
    private Long userId;

    @Indexed
    private String token;

    private LocalDateTime createdAt;
}