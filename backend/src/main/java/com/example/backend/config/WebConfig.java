package com.example.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload.path:./upload}")
    private String uploadPath;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path path = Paths.get(uploadPath).toAbsolutePath().normalize();
        File uploadDir = path.toFile();
        
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        
        String location = "file:" + path.toString() + "/";
        
        registry.addResourceHandler("/upload/**")
                .addResourceLocations(location);
        
        System.out.println("========================================");
        System.out.println("静态资源配置:");
        System.out.println("  配置路径: " + uploadPath);
        System.out.println("  绝对路径: " + path);
        System.out.println("  访问地址: /upload/**");
        System.out.println("  资源位置: " + location);
        System.out.println("  目录存在: " + uploadDir.exists());
        System.out.println("========================================");
    }
}
