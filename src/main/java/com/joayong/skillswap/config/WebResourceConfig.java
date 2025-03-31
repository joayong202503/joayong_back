package com.joayong.skillswap.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// 로컬에 저장된 파일을 서버에서 열 수 있도록 설정
@Configuration
@RequiredArgsConstructor
public class WebResourceConfig implements WebMvcConfigurer {

    private  final  FileUploadConfig fileUploadConfig;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**") //서버 url
                .addResourceLocations("file:" + fileUploadConfig.getLocation());    //로컬 url
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Forward all requests to index.html except for /api/** and /uploads/**
        registry.addViewController("/**/{path:[^\\.]*}")
                .setViewName("forward:/");
    }
}
