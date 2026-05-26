package com.club.fund.service;

import com.club.fund.config.UploadConfig;
import com.club.fund.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class FileService {

    @Autowired
    private UploadConfig uploadConfig;

    public Map<String, Object> uploadImage(MultipartFile file, String type) {
        if (file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);

        if (!isAllowedType(extension)) {
            throw new BusinessException("不支持的文件类型");
        }

        if (file.getSize() > uploadConfig.getMaxSize()) {
            throw new BusinessException("文件大小超过限制");
        }

        String newFileName = UUID.randomUUID().toString() + "." + extension;
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String relativePath = "/" + type + "/" + datePath + "/" + newFileName;
        Path basePath = Path.of(uploadConfig.getPath()).toAbsolutePath().normalize();
        Path targetPath = basePath.resolve(type).resolve(datePath).resolve(newFileName).normalize();

        try {
            Files.createDirectories(targetPath.getParent());
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            log.error("文件上传失败，目标路径: {}, 原因: {}", targetPath, e.getMessage(), e);
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("url", relativePath);
        result.put("fileName", newFileName);
        result.put("fileSize", file.getSize());

        return result;
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1).toLowerCase();
    }

    private boolean isAllowedType(String extension) {
        if (extension == null || extension.isEmpty()) {
            return false;
        }
        String[] allowedTypes = uploadConfig.getAllowedTypes().split(",");
        return Arrays.asList(allowedTypes).contains(extension.toLowerCase());
    }
}
