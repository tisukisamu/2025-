package com.club.fund.config;

import com.club.fund.interceptor.ApiLogInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final ApiLogInterceptor apiLogInterceptor;

    @Value("${upload.path}")
    private String uploadPath;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String absoluteUploadPath = Path.of(uploadPath).toAbsolutePath().normalize().toUri().toString();
        
        // 处理 /upload/** 路径
        registry.addResourceHandler("/upload/**")
                .addResourceLocations(absoluteUploadPath);
        
        // 处理 /avatar/** 路径
        registry.addResourceHandler("/avatar/**")
                .addResourceLocations(absoluteUploadPath + "avatar/");
        
        // 处理 /voucher/** 路径
        registry.addResourceHandler("/voucher/**")
                .addResourceLocations(absoluteUploadPath + "voucher/");
        
        // 处理 /logo/** 路径
        registry.addResourceHandler("/logo/**")
                .addResourceLocations(absoluteUploadPath + "logo/");
        
        // 处理 /cover/** 路径
        registry.addResourceHandler("/cover/**")
                .addResourceLocations(absoluteUploadPath + "cover/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(apiLogInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/log/**", "/api/auth/login", "/api/auth/register", "/error", 
                        "/upload/**", "/avatar/**", "/voucher/**", "/logo/**", "/cover/**");
    }
}
