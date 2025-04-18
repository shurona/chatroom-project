package com.shurona.chat.mytalk.user.common.config;

import com.shurona.chat.mytalk.user.presentation.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SpringWebConfig implements WebMvcConfigurer {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
            .order(1)
            .addPathPatterns("/**")
            .excludePathPatterns("/", "/home", "/login/v1", "/users/v1/form",
                "/logout", "/css/**", "/*.ico", "/error");
    }
}
