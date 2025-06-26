package com.shurona.chat.mytalk.common.config;

import com.shurona.chat.mytalk.common.filter.JwtAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthorizationFilter jwtAuthorizationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // CSRF 설정 및 시큐리티 기본 설정 비활성화
        http.csrf(AbstractHttpConfigurer::disable);

        // 경로별 인가 작업
        http
            .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
                .requestMatchers(
                    "/api/v1/users/sign-up", "/api/v1/login", "/api/v1/refresh") // 회원 가입, 로그인, 리프레시
                .permitAll()
                .requestMatchers(
                    "/home", "/ssr/**", "/socket/chat", "/css/**", "/*.ico", "/error")
                .permitAll() // 기존 friends, chats page 접근 허용 설정
                .anyRequest()
                .authenticated()
            )

            // 기본 설정인 Session 방식은 사용하지 않고 JWT 방식을 사용하기 위한 설정
            // 세션을 stateless상태로 관리!
            .sessionManagement((sessionManagement) ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );

        // JWT 인증 필터를 UsernamePasswordAuthenticationFilter 앞에 추가
        http.addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();

    }

}
