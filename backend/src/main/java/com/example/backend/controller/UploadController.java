package com.example.backend.controller;

import com.example.backend.dto.response.ApiResponse;
import com.example.backend.exception.BusinessException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "*")
public class UploadController {

    @Value("${file.upload-path:./uploads}")
    private String configuredUploadPath;

    private Path uploadPath;

    @PostConstruct
    public void init() {
        File uploadDir = new File(configuredUploadPath);
        if (!uploadDir.isAbsolute()) {
            String userDir = System.getProperty("user.dir");
            uploadPath = Paths.get(userDir, configuredUploadPath);
        } else {
            uploadPath = Paths.get(configuredUploadPath);
        }
        
        try {
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            log.info("[Upload] Upload directory initialized: {}", uploadPath.toAbsolutePath());
        } catch (IOException e) {
            log.error("[Upload] Failed to create upload directory: {}", e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<String>> uploadFile(@RequestParam("file") MultipartFile file) {
        log.info("[Upload] Received file: {}, size: {}", file.getOriginalFilename(), file.getSize());
        
        if (file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }

        try {
            String fileName = generateFileName(file.getOriginalFilename());
            
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);
            
            String fileUrl = "/uploads/" + fileName;
            log.info("[Upload] File saved successfully: {}", filePath.toAbsolutePath());
            
            return ResponseEntity.ok(ApiResponse.success("上传成功", fileUrl));
            
        } catch (IOException e) {
            log.error("[Upload] Failed to upload file: {}", e.getMessage(), e);
            throw new BusinessException("文件上传失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("[Upload] Unexpected error: {}", e.getMessage(), e);
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }
    }

    @PostMapping("/multiple")
    public ResponseEntity<ApiResponse<List<String>>> uploadFiles(@RequestParam("files") MultipartFile[] files) {
        log.info("[Upload] Received {} files", files != null ? files.length : 0);
        
        if (files == null || files.length == 0) {
            throw new BusinessException("上传文件不能为空");
        }

        List<String> fileUrls = new ArrayList<>();
        
        try {
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String fileName = generateFileName(file.getOriginalFilename());
                    Path filePath = uploadPath.resolve(fileName);
                    Files.copy(file.getInputStream(), filePath);
                    fileUrls.add("/uploads/" + fileName);
                    log.info("[Upload] File saved: {}", fileName);
                }
            }
            
            log.info("[Upload] Successfully uploaded {} files", fileUrls.size());
            return ResponseEntity.ok(ApiResponse.success("上传成功", fileUrls));
            
        } catch (IOException e) {
            log.error("[Upload] Failed to upload files: {}", e.getMessage(), e);
            throw new BusinessException("文件上传失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("[Upload] Unexpected error: {}", e.getMessage(), e);
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }
    }

    private String generateFileName(String originalFileName) {
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        return UUID.randomUUID().toString() + extension;
    }
}
