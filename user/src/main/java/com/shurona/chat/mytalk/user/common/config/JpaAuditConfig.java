package com.shurona.chat.mytalk.user.common.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
@EnableJpaAuditing
public class JpaAuditConfig implements AuditorAware<Long> {

    @Override
    public Optional<Long> getCurrentAuditor() {

        ServletRequestAttributes attributes
            = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        Long userId = -1L;
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();

            // request의 세션에서 userId를 갖고 온다.
            HttpSession session = request.getSession();
            Long sessionUserId = (Long) session.getAttribute("userId");
            if (sessionUserId != null) {
                userId = sessionUserId;
            }
        }

        return Optional.of(userId);
    }
}
