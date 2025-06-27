package com.shurona.chat.mytalk.common.config;

import com.shurona.chat.mytalk.user.presentation.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SpringWebConfig implements WebMvcConfigurer {

//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//            .allowedOrigins("http://localhost:3000") // 허용할 출처 : 특정 도메인만 받을 수 있음
//            .allowedMethods("GET", "POST", "PATCH", "DELETE", "PUT") // 허용할 HTTP method
//            .allowCredentials(true);
//    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
            .order(1)
            .addPathPatterns("/ssr/**")
            .excludePathPatterns("/", "/home", "/ssr/login/v1", "/ssr/users/v1/form",
                "/ssr/logout/v1", "/css/**", "/*.ico", "/error");
    }
}
