package com.todaysroom.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    // 서버 내렸다 올려야 정적 리소스 이미지가 배포되기 때문에 구현

    // 정적인 리소스에 대한 요청을 처리하는 핸들러, 정적 파일들의 경로를 잡아주는 메서드
    // addResourceHandler에 정의한 루트로 들어오는 모든 정적 리소스 요청을
    // addResourceLocations에서 정의한 경로에서 찾는다는 의미.
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("file:src/main/resources/static/");
    }
}
