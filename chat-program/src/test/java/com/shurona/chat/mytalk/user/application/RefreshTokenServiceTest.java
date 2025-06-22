package com.shurona.chat.mytalk.user.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.shurona.chat.mytalk.common.dto.redis.RefreshToken;
import com.shurona.chat.mytalk.user.application.token.RefreshTokenService;
import com.shurona.chat.mytalk.user.common.utils.JwtUtils;
import com.shurona.chat.mytalk.user.infrastructure.RefreshTokenRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Test
    @DisplayName("리프레시 토큰 생성 테스트")
    void createAndSaveRefreshTokenTest() {
        // given
        Long userId = 1L;
        String mockToken = "mock.refresh.token";
        RefreshToken mockRefreshToken = RefreshToken.builder()
            .userId(userId)
            .token(mockToken)
            .createdAt(LocalDateTime.now())
            .build();

        when(jwtUtils.createRefreshToken(userId)).thenReturn(mockToken);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(mockRefreshToken);

        // when
        String refreshToken = refreshTokenService.createAndSaveRefreshToken(userId);

        // then
        assertThat(refreshToken).isEqualTo(mockToken);
        verify(refreshTokenRepository).findById(userId);
        verify(jwtUtils).createRefreshToken(userId);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("토큰으로 리프레시 토큰 조회 테스트")
    void findByTokenTest() {
        // given
        String token = "mock.refresh.token";
        Long userId = 1L;
        RefreshToken mockRefreshToken = RefreshToken.builder()
            .userId(userId)
            .token(token)
            .createdAt(LocalDateTime.now())
            .build();

        when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.of(mockRefreshToken));

        // when
        Optional<RefreshToken> result = refreshTokenService.findByToken(token);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getUserId()).isEqualTo(userId);
        assertThat(result.get().getToken()).isEqualTo(token);
        verify(refreshTokenRepository).findByToken(token);
    }

    @Test
    @DisplayName("사용자 ID로 리프레시 토큰 조회 테스트")
    void findByUserIdTest() {
        // given
        Long userId = 1L;
        String token = "mock.refresh.token";
        RefreshToken mockRefreshToken = RefreshToken.builder()
            .userId(userId)
            .token(token)
            .createdAt(LocalDateTime.now())
            .build();

        when(refreshTokenRepository.findById(userId)).thenReturn(Optional.of(mockRefreshToken));

        // when
        Optional<RefreshToken> result = refreshTokenService.findByUserId(userId);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getUserId()).isEqualTo(userId);
        assertThat(result.get().getToken()).isEqualTo(token);
        verify(refreshTokenRepository).findById(userId);
    }
}