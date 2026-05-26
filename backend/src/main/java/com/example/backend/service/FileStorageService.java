package com.example.backend.service;

import com.example.backend.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class FileStorageService {

    public String storeImage(MultipartFile file, String category) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请选择要上传的图片");
        }
        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            throw new BusinessException("仅支持图片文件上传");
        }

        String safeCategory = StringUtils.hasText(category) ? category.replaceAll("[^a-zA-Z0-9_-]", "") : "common";
        String extension = getExtension(file.getOriginalFilename());
        String fileName = UUID.randomUUID() + extension;
        LocalDate now = LocalDate.now();
        Path root = Paths.get("upload").toAbsolutePath().normalize();
        Path targetDir = root.resolve(safeCategory).resolve(String.valueOf(now.getYear())).resolve(String.format("%02d", now.getMonthValue()));

        try {
            Files.createDirectories(targetDir);
            Path targetPath = targetDir.resolve(fileName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/" + safeCategory + "/" + now.getYear() + "/" + String.format("%02d", now.getMonthValue()) + "/" + fileName;
        } catch (IOException e) {
            throw new BusinessException("图片上传失败");
        }
    }

    private String getExtension(String originalFilename) {
        if (!StringUtils.hasText(originalFilename) || !originalFilename.contains(".")) {
            return ".png";
        }
        String ext = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        if (ext.length() > 10) {
            return ".png";
        }
        return ext;
    }
}
