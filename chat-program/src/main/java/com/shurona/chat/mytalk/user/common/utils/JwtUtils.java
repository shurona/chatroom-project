package com.shurona.chat.mytalk.user.common.utils;

import static com.shurona.chat.mytalk.user.common.exception.UserErrorCode.JWT_TOKEN_INVALID;

import com.shurona.chat.mytalk.user.common.exception.UserException;
import com.shurona.chat.mytalk.user.domain.type.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Base64;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class JwtUtils {

    public static final String AUTHORIZATION_HEADER = "Authorization";

    // Jwt payload에서 사용자 권한 값의 KEY
    public static final String AUTHORIZATION_KEY = "auth";

    // Token 식별자
    public static final String BEARER_PREFIX = "Bearer ";

    public static final long TOKEN_TIME = 24 * 60 * 60 * 1000L; // 60분


    private String secretKey;
    private SecretKey key;

    public JwtUtils(@Value("${jwt.secret.key}") String secretKey) {
        this.secretKey = secretKey;
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    public String createToken(Long userId, UserRole role) {
        Date date = new Date();

        return BEARER_PREFIX +
            Jwts.builder()
                .subject(String.valueOf(userId)) // 사용자 식별자값(ID)
                .claim(AUTHORIZATION_KEY, role.getAuthority()) // 사용자 권한
                .expiration(new Date(date.getTime() + TOKEN_TIME)) // 만료 시간
                .issuedAt(date) // 발급일
                .signWith(key) // 암호화
                .compact();
    }


    public String substringToken(String tokenValue) {
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            return tokenValue.substring(7);
        }

        throw new UserException(JWT_TOKEN_INVALID);
    }

    /**
     * 토큰의 Validation 확인
     */
    public boolean checkValidJwtToken(String jwtToken) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(jwtToken);
            return true;
        } catch (Exception e) {
            log.error("에러 원인 : {} 에러 설명 : {}", e.getCause(), e.getMessage());
        }
        return false;
    }

    /**
     * payload 정보 갖고 오기
     */
    public Claims getBodyFromJwt(String jwtToken) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(jwtToken).getPayload();
    }

}
