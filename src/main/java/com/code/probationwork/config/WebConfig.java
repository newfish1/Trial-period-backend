package com.code.probationwork.config;

import com.code.probationwork.interceptor.LoginInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Resource
    private LoginInterceptor loginInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .excludePathPatterns(
                        "/api/user/login",
                        "/api/user/reg",
                        "/api/student/image",
                        "/images/**",
                        "/static/**",
                        "/favicon.ico",
                        "/.well-known/appspecific/com.chrome.devtools.json"
                );
    }
}

