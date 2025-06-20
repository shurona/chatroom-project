package com.shurona.chat.mytalk.common.config;

import com.shurona.chat.mytalk.user.presentation.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SpringWebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
            .order(1)
            .addPathPatterns("/ssr/**")
            .excludePathPatterns("/", "/home", "/ssr/login/v1", "/ssr/users/v1/form",
                "/ssr/logout/v1", "/css/**", "/*.ico", "/error");
    }
}
