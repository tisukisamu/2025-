package com.example.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class FileUploadConfig implements WebMvcConfigurer {
    
    @Value("${file.upload.path:upload}")
    private String uploadPathConfig;
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadPath = findUploadPath();
        System.out.println("静态资源目录: " + uploadPath);
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:" + uploadPath.toString() + "/");
    }
    
    private Path findUploadPath() {
        Path backendPath = Paths.get(System.getProperty("user.dir"), uploadPathConfig).toAbsolutePath();
        if (Files.exists(backendPath)) {
            return backendPath;
        }
        
        Path projectRootPath = Paths.get(System.getProperty("user.dir")).getParent();
        if (projectRootPath != null) {
            Path rootUploadPath = projectRootPath.resolve(uploadPathConfig).toAbsolutePath();
            if (Files.exists(rootUploadPath)) {
                return rootUploadPath;
            }
        }
        
        return backendPath;
    }
}
