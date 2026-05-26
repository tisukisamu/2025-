package com.example.backend.config;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.MultipartConfigElement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class FileUploadConfig {

    @Value("${file.upload.path:./upload}")
    private String uploadPath;

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.ofMegabytes(50));
        factory.setMaxRequestSize(DataSize.ofMegabytes(100));
        return factory.createMultipartConfig();
    }

    @PostConstruct
    public void initUploadDirectory() {
        Path path = Paths.get(uploadPath).toAbsolutePath().normalize();
        File uploadDir = path.toFile();
        
        if (!uploadDir.exists()) {
            boolean created = uploadDir.mkdirs();
            System.out.println("创建上传目录: " + path + " - " + (created ? "成功" : "失败"));
        }
        
        String[] subDirs = {"pets", "services", "memorials", "users"};
        for (String subDir : subDirs) {
            File dir = new File(uploadDir, subDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }
        
        System.out.println("========================================");
        System.out.println("文件上传配置:");
        System.out.println("  上传目录: " + path);
        System.out.println("  目录存在: " + uploadDir.exists());
        System.out.println("========================================");
    }
}
