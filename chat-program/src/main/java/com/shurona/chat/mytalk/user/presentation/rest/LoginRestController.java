package com.shurona.chat.mytalk.user.presentation.rest;

import com.shurona.chat.mytalk.user.application.UserService;
import com.shurona.chat.mytalk.user.common.utils.JwtUtils;
import com.shurona.chat.mytalk.user.domain.model.User;
import com.shurona.chat.mytalk.user.presentation.dto.request.LoginRequestDto;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/")
public class LoginRestController {

    private final UserService userService;
    private final JwtUtils jwtUtils;

    /**
     * 로그인 API
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(
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

        // 응답 헤더에 토큰 추가
        response.addHeader(JwtUtils.AUTHORIZATION_HEADER, token);

        return ResponseEntity.ok(token);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
//         userService.logout();
        return ResponseEntity.ok().build();
    }

}
