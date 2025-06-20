package com.shurona.chat.mytalk.user.presentation.rest;

import com.shurona.chat.mytalk.common.dto.ErrorResponseDto;
import com.shurona.chat.mytalk.common.security.UserDetailsImpl;
import com.shurona.chat.mytalk.user.application.UserService;
import com.shurona.chat.mytalk.user.domain.model.User;
import com.shurona.chat.mytalk.user.presentation.dto.response.UserResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1/users")
public class UserRestController {

    private final UserService userService;

    @PreAuthorize("hasRole('ROLE_ADMIN') or #userDetails.userId() == #id")
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable("id") Long id) {

        User user = userService.findUserById(id);
        return ResponseEntity.ok(UserResponseDto.from(user)); // 사용자 정보를 반환
    }


    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponseDto> handleAuthorizationDeniedException(
        AuthorizationDeniedException e) {
        return ResponseEntity.status(403)
            .body(new ErrorResponseDto(e.getMessage())); // 403 Forbidden 응답
    }
}
