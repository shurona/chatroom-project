package com.shurona.chat.mytalk.user.common.utils;

import com.shurona.chat.mytalk.user.domain.type.UserRole;
import io.jsonwebtoken.Claims;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class JwtUtilsTest {

    private final String TEST_JWT_KEY = "7Leo7JeF7Leo7JeF7Leo7JeF7Leo7JeF7Leo7JeF7Leo7JeF7Leo7JeF";

    @Test
    public void JWT_생성_검증() {
        // given
        JwtUtils jwtUtils = new JwtUtils(TEST_JWT_KEY);

        Long userId = 1L;

        String token = jwtUtils.createToken(userId, UserRole.ADMIN);

        // when
        boolean validToken = jwtUtils.checkValidJwtToken(token.substring(7));
        boolean InvalidToken = jwtUtils.checkValidJwtToken(token);

        // then
        Assertions.assertThat(validToken).isTrue();
        Assertions.assertThat(InvalidToken).isFalse();
    }

    @Test
    public void JWT_생성_조회() {
        // given
        JwtUtils jwtUtils = new JwtUtils(TEST_JWT_KEY);

        Long userId = 1L;

        String token = jwtUtils.createToken(userId, UserRole.ADMIN);

        // when
        Claims bodyFromJwt = jwtUtils.getBodyFromJwt(token.substring(7));

        // then
        Assertions.assertThat(String.valueOf(userId)).isEqualTo(bodyFromJwt.getSubject());
        Assertions.assertThat(UserRole.ADMIN.getAuthority())
            .isEqualTo(bodyFromJwt.get(JwtUtils.AUTHORIZATION_KEY));

    }

}