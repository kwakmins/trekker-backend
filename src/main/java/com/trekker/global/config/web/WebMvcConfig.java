package com.trekker.global.config.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private static final String FILE_PATH = "/uploads/profile-images/**";
    private static final String FILE_PROTOCOL = "file:";

    @Value("${file.dir}")
    private String UPLOAD_DIR;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // /uploads/profile-images/** 경로에 대한 요청을 파일 시스템의 업로드 디렉토리로 매핑
        registry.addResourceHandler(FILE_PATH)
                .addResourceLocations(FILE_PROTOCOL + UPLOAD_DIR);
    }
}
