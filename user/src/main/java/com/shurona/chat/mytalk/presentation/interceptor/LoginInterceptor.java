package com.shurona.chat.mytalk.presentation.interceptor;

import static com.shurona.chat.mytalk.common.variable.StaticVariable.LOGIN_USER;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) throws Exception {

        String requestURI = request.getRequestURI();
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute(LOGIN_USER) == null) {
            // 미인증 사용자
            response.sendRedirect("/?redirectURL=" + requestURI);
            return false;
        }

        return true;

    }
}
