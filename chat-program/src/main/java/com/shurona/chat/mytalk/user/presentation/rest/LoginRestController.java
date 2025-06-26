package com.shurona.chat.mytalk.user.presentation.rest;

import com.shurona.chat.mytalk.common.dto.CommonResponseDto;
import com.shurona.chat.mytalk.user.application.UserService;
import com.shurona.chat.mytalk.user.application.token.RedisTokenService;
import com.shurona.chat.mytalk.user.common.utils.JwtUtils;
import com.shurona.chat.mytalk.user.domain.model.User;
import com.shurona.chat.mytalk.user.presentation.dto.request.LoginRefreshRequestBody;
import com.shurona.chat.mytalk.user.presentation.dto.request.LoginRequestDto;
import com.shurona.chat.mytalk.user.presentation.dto.request.LogoutRequestDto;
import com.shurona.chat.mytalk.user.presentation.dto.response.LoginResponseDto;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/")
public class LoginRestController {

    private final UserService userService;
    private final RedisTokenService redisTokenService;
    private final JwtUtils jwtUtils;

    /**
     * 로그인 API
     */
    @PostMapping("/login")
    public ResponseEntity<CommonResponseDto<LoginResponseDto>> login(
        @RequestBody LoginRequestDto request,
        HttpServletResponse response
    ) {
        User userInfo = userService.findUserByLoginId(request.loginId());
        if (userInfo == null) {
            return ResponseEntity.status(401).build(); // Unauthorized
        }
        if (!userService.checkPasswordCorrect(request.password(), userInfo.getPassword())) {
            return ResponseEntity.status(401).build(); // Unauthorized
        }

        // jwt 토큰 생성
        String token = jwtUtils.createToken(userInfo.getId(), userInfo.getUserRole());
        // Refresh 토큰 생성 및 저장
        String refreshToken = redisTokenService.createAndSaveRefreshToken(userInfo.getId());

        // 응답 헤더에 토큰 추가
        response.addHeader(JwtUtils.AUTHORIZATION_HEADER, token);

        return ResponseEntity.ok(
            CommonResponseDto.ofData(
                new LoginResponseDto(
                    token,
                    refreshToken
                ))
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
        @RequestBody LogoutRequestDto requestDto
    ) {
        // redis의 refresh Token 삭제
        redisTokenService.deleteByUserId(requestDto.userId());

        log.info("[로그아웃 완료] 유저 아이디 : {}", requestDto.userId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<CommonResponseDto<LoginResponseDto>> refreshToken(
        @RequestBody LoginRefreshRequestBody refreshToken,
        HttpServletResponse response
    ) {
        // Refresh 토큰 검증 및 새 토큰 생성
        Optional<Long> userId = redisTokenService.findUserIdByToken(refreshToken.refreshToken());
        if (userId.isEmpty()) {
            return ResponseEntity.status(401).build(); // Unauthorized
        }

        User userInfo = userService.findUserById(userId.get());

        String newToken = jwtUtils.createToken(userInfo.getId(), userInfo.getUserRole());

        // 응답 헤더에 새 토큰 추가
        response.addHeader(JwtUtils.AUTHORIZATION_HEADER, newToken);

        return ResponseEntity.ok(
            CommonResponseDto.ofData(new LoginResponseDto(
                newToken,
                refreshToken.refreshToken() // 기존 Refresh 토큰 그대로 사용
            ))
        );
    }

}
