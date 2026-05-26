package com.example.backend.util;

import com.example.backend.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
public class FileUploadUtil {
    
    @Value("${file.upload-dir:./upload}")
    private String uploadDir;
    
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
        "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );
    
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    
    public String uploadFile(MultipartFile file, String subDir) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }
        
        if (!isValidFileSize(file, MAX_FILE_SIZE)) {
            throw new BusinessException("文件大小不能超过10MB");
        }
        
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        String newFilename = UUID.randomUUID().toString() + fileExtension;
        
        try {
            Path uploadPath = Paths.get(uploadDir, subDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);
            
            Path targetLocation = uploadPath.resolve(newFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            
            return "/" + subDir + "/" + newFilename;
        } catch (IOException ex) {
            throw new BusinessException("文件上传失败: " + ex.getMessage());
        }
    }
    
    public String uploadAvatar(MultipartFile file) {
        if (!isValidImageFile(file)) {
            throw new BusinessException("只支持JPEG、PNG、GIF、WEBP格式的图片");
        }
        return uploadFile(file, "avatars");
    }
    
    public String uploadCourseImage(MultipartFile file) {
        if (!isValidImageFile(file)) {
            throw new BusinessException("只支持JPEG、PNG、GIF、WEBP格式的图片");
        }
        return uploadFile(file, "course-images");
    }
    
    public void deleteFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return;
        }
        
        try {
            Path fileToDelete = Paths.get(uploadDir, filePath).toAbsolutePath().normalize();
            Files.deleteIfExists(fileToDelete);
        } catch (IOException ex) {
            throw new BusinessException("文件删除失败: " + ex.getMessage());
        }
    }
    
    public boolean isValidImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase());
    }
    
    public boolean isValidFileSize(MultipartFile file, long maxSize) {
        return file.getSize() <= maxSize;
    }
}
