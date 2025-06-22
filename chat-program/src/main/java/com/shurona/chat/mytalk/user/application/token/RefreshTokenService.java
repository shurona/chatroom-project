package com.shurona.chat.mytalk.user.application.token;

import com.shurona.chat.mytalk.common.dto.redis.RefreshToken;
import com.shurona.chat.mytalk.user.common.utils.JwtUtils;
import com.shurona.chat.mytalk.user.infrastructure.RefreshTokenRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtils jwtUtils;

    public RefreshToken saveRefreshToken(Long userId, String token) {
        RefreshToken refreshToken = RefreshToken.builder()
            .userId(userId)
            .token(token)
            .createdAt(LocalDateTime.now())
            .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public Optional<RefreshToken> findByUserId(Long userId) {
        return refreshTokenRepository.findById(userId);
    }

    public void deleteByUserId(Long userId) {
        refreshTokenRepository.deleteById(userId);
    }

    // JWT 생성 및 Redis 저장을 처리하는 통합 메서드
    public String createAndSaveRefreshToken(Long userId) {
        // 기존 토큰이 있으면 삭제
        refreshTokenRepository.findById(userId).ifPresent(token ->
            refreshTokenRepository.deleteById(userId));

        // 새 토큰 생성
        String refreshToken = jwtUtils.createRefreshToken(userId);

        // Redis에 저장
        saveRefreshToken(userId, refreshToken);

        return refreshToken;
    }
}
