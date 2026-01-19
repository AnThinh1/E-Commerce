package com.codegym.ecommercemanage.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.time.Duration;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }

    @Bean
    public RestClient restClient() {
        // KHÔNG DÙNG tham số (RestClient.Builder builder) nữa để tránh lỗi Autowire
        // Dùng hàm tĩnh RestClient.builder() để tự tạo mới
        return RestClient.builder()
                .baseUrl("http://localhost:8000/api")
                .build();
    }
}
