package com.shurona.chat.mytalk.common.filter;

import com.shurona.chat.mytalk.common.security.UserDetailsImpl;
import com.shurona.chat.mytalk.user.common.exception.UserErrorCode;
import com.shurona.chat.mytalk.user.common.exception.UserException;
import com.shurona.chat.mytalk.user.common.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JwtAuthorizationFilter 클래스는 JWT 토큰을 검증하고 인증 객체를 설정하는 필터입니다.
 * OncePerRequestFilter를 상속받아 매 요청마다 실행됩니다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        String token = request.getHeader("Authorization"); // 요청 헤더에서 JWT 토큰을 가져옵니다.
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // "Bearer " 접두사를 제거합니다.
            try {
                // JWT 토큰에서 사용자 ID와 권한을 추출합니다.
                if (!jwtUtils.checkValidJwtToken(token)) {
                    // 토큰이 유효하지 않은 경우 예외를 발생시킵니다.
                    throw new UserException(UserErrorCode.JWT_TOKEN_INVALID);
                }

                Claims bodyFromJwt = jwtUtils.getBodyFromJwt(token);

                Long userId = Long.parseLong(bodyFromJwt.getSubject()); // 사용자 ID 추출
                String role = bodyFromJwt.get(JwtUtils.AUTHORIZATION_KEY,
                    String.class); // 사용자 권한 추출

                // 인증 객체를 생성하고 SecurityContext에 설정합니다.
                setAuthentication(userId, role);
            } catch (Exception e) {
                log.error("JWT 토큰 검증 실패: {}", e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
                return; // 인증 실패 시 필터 체인을 중단합니다.
            }
        }
        filterChain.doFilter(request, response);
    }

    /**
     * 인증 객체를 설정합니다.
     */
    private void setAuthentication(Long userId, String role) {
        Authentication authentication = createAuthentication(userId, role);
        // SecurityContextHolder에 인증 객체를 설정합니다.
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * 인증 객체를 생성합니다.
     */
    private Authentication createAuthentication(Long userId, String role) {
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(role); // 권한 생성
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>(); // 권한 컬렉션 생성
        authorities.add(simpleGrantedAuthority); // 권한 추가

        return new UsernamePasswordAuthenticationToken(
            new UserDetailsImpl(userId, authorities, role), null, authorities);
    }

}
