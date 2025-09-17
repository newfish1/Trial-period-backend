package com.code.probationwork.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ImageConfig implements WebMvcConfigurer {
    @Value("${save.path}")
    private String savePath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置静态资源映射，使前端可以访问上传的图片
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + savePath + "/");
    }
}
