package com.shurona.chat.mytalk.user.application.token;

import com.shurona.chat.mytalk.user.common.utils.JwtUtils;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RedisTokenService {

    private static final String USER_KEY_PREFIX = "refresh:user:";  // 사용자 ID로 토큰 조회
    private static final String TOKEN_KEY_PREFIX = "refresh:token:"; // 토큰 값으로 사용자 ID
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtUtils jwtUtils;

    /**
     * 리프레시 토큰 저장
     */
    public void saveRefreshToken(Long userId, String token) {
        String userKey = USER_KEY_PREFIX + userId;
        String tokenKey = TOKEN_KEY_PREFIX + token;

        long expirationTime = JwtUtils.REFRESH_TOKEN_TIME / 1000; // 초 단위로 변환

        // 사용자 ID로 토큰 조회
        redisTemplate.opsForValue()
            .set(userKey, token, expirationTime, TimeUnit.SECONDS);
        // 토큰으로 사용자 ID 조회
        redisTemplate.opsForValue()
            .set(tokenKey, userId.toString(), expirationTime, TimeUnit.SECONDS);
    }

    /**
     * 토큰으로 사용자 ID 조회
     */
    public Optional<Long> findUserIdByToken(String token) {
        String tokenKey = TOKEN_KEY_PREFIX + token;
        String userId = redisTemplate.opsForValue().get(tokenKey);

        return Optional.ofNullable(userId)
            .map(Long::valueOf);
    }

    /**
     * 사용자 ID로 토큰 조회
     */
    public Optional<String> findTokenByUserId(Long userId) {
        String userKey = USER_KEY_PREFIX + userId;
        String token = redisTemplate.opsForValue().get(userKey);

        return Optional.ofNullable(token);
    }

    /**
     * 사용자 ID로 토큰 삭제
     */
    public void deleteByUserId(Long userId) {
        String userKey = USER_KEY_PREFIX + userId;

        // 기존 토큰 조회
        String token = redisTemplate.opsForValue().get(userKey);
        if (token != null) {
            String tokenKey = TOKEN_KEY_PREFIX + token;
            // 토큰 인덱스 삭제
            redisTemplate.delete(tokenKey);
        }

        // 사용자 키 삭제
        redisTemplate.delete(userKey);
    }

    /**
     * 토큰으로 삭제
     */
    public void deleteByToken(String token) {
        String tokenKey = TOKEN_KEY_PREFIX + token;

        // 토큰으로 사용자 ID 조회
        String userId = redisTemplate.opsForValue().get(tokenKey);
        if (userId != null) {
            String userKey = USER_KEY_PREFIX + userId;
            // 사용자 키 삭제
            redisTemplate.delete(userKey);
        }

        // 토큰 키 삭제
        redisTemplate.delete(tokenKey);
    }

    /**
     * 리프레시 토큰 생성 및 저장
     */
    public String createAndSaveRefreshToken(Long userId) {
        // 기존 토큰 삭제
        deleteByUserId(userId);

        // 새 토큰 생성
        String refreshToken = jwtUtils.createRefreshToken(userId);

        // Redis에 저장
        saveRefreshToken(userId, refreshToken);

        return refreshToken;
    }


}
