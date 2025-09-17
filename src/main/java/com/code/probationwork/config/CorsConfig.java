package com.code.probationwork.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Collections;


import java.util.Collections;

//@Configuration
//public class CorsConfig implements WebMvcConfigurer {
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                // 允许的来源，*表示允许所有域名
//                .allowedOrigins("*")
//                // 允许的HTTP方法
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//                // 允许的请求头
//                .allowedHeaders("*")
//                // 是否允许携带凭证（如Cookie）
//                .allowCredentials(true)
//                // 预检请求的缓存时间（秒），减少OPTIONS请求频率
//                .maxAge(3600);
//    }
//}

@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        //1.允许任何来源
        corsConfiguration.setAllowedOriginPatterns(Collections.singletonList("*"));
        //2.允许任何请求头
        corsConfiguration.addAllowedHeader(CorsConfiguration.ALL);
        //3.允许任何方法
        corsConfiguration.addAllowedMethod(CorsConfiguration.ALL);
        //4.允许凭证
        corsConfiguration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(source);
    }
}

//@Configuration
//public class CorsConfig {
//
//    @Bean
//    public FilterRegistrationBean filterRegistrationBean() {
//        CorsConfiguration corsConfiguration = new CorsConfiguration();
//        //1.允许任何来源
//        corsConfiguration.setAllowedOriginPatterns(Collections.singletonList("*"));
//        //2.允许任何请求头
//        corsConfiguration.addAllowedHeader(CorsConfiguration.ALL);
//        //3.允许任何方法
//        corsConfiguration.addAllowedMethod(CorsConfiguration.ALL);
//        //4.允许凭证
//        corsConfiguration.setAllowCredentials(true);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", corsConfiguration);
//        CorsFilter corsFilter = new CorsFilter(source);
//
//        FilterRegistrationBean<CorsFilter> filterRegistrationBean=new FilterRegistrationBean<>(corsFilter);
//        filterRegistrationBean.setOrder(-101);  // 小于 SpringSecurity Filter的 Order(-100) 即可
//
//        return filterRegistrationBean;
//    }
//}