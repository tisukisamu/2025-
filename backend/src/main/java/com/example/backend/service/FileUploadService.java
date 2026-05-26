package com.example.backend.service;

import com.example.backend.exception.BusinessException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
public class FileUploadService {
    
    @Value("${file.upload.path:upload}")
    private String uploadPathConfig;
    
    private Path uploadPath;
    
    @PostConstruct
    public void init() {
        this.uploadPath = findUploadPath();
        log.info("文件上传目录: {}", uploadPath);
        
        try {
            Files.createDirectories(uploadPath);
            log.info("上传目录创建成功");
        } catch (IOException e) {
            log.error("创建上传目录失败", e);
        }
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
    
    public String uploadImage(MultipartFile file, String type) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(400, "上传文件不能为空");
        }
        
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new BusinessException(400, "文件名不能为空");
        }
        
        String fileExtension = getFileExtension(originalFilename);
        if (!isValidImageExtension(fileExtension)) {
            throw new BusinessException(400, "只支持 JPG、PNG 格式的图片");
        }
        
        if (file.getSize() > 2 * 1024 * 1024) {
            throw new BusinessException(400, "图片大小不能超过 2MB");
        }
        
        try {
            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
            String fileName = UUID.randomUUID().toString() + "." + fileExtension;
            String relativePath = type + "/" + datePath + "/" + fileName;
            
            Path directoryPath = uploadPath.resolve(type).resolve(datePath);
            Files.createDirectories(directoryPath);
            
            Path filePath = uploadPath.resolve(relativePath);
            file.transferTo(filePath.toFile());
            
            log.info("文件上传成功: {}", relativePath);
            return "/upload/" + relativePath;
            
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new BusinessException(500, "文件上传失败: " + e.getMessage());
        }
    }
    
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }
        
        try {
            String relativePath = fileUrl.replace("/upload/", "");
            Path filePath = uploadPath.resolve(relativePath);
            Files.deleteIfExists(filePath);
            log.info("文件删除成功: {}", relativePath);
        } catch (IOException e) {
            log.error("文件删除失败", e);
        }
    }
    
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1).toLowerCase();
    }
    
    private boolean isValidImageExtension(String extension) {
        return "jpg".equals(extension) || "jpeg".equals(extension) || "png".equals(extension);
    }
}
