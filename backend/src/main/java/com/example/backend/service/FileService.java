package com.example.backend.service;

import com.example.backend.entity.FileRecord;
import com.example.backend.exception.BusinessException;
import com.example.backend.repository.FileRecordRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    @Value("${file.upload.path:./upload}")
    private String uploadPath;

    @Value("${file.upload.allowed-types:jpg,jpeg,png,gif,webp,pdf,mp4,webm}")
    private String allowedTypes;

    @Value("${file.upload.max-size:52428800}")
    private Long maxSize;

    private final FileRecordRepository fileRecordRepository;

    @Transactional
    public String uploadFile(MultipartFile file, String entityType, Long entityId, Long uploaderId) {
        validateFile(file);

        String originalFilename = file.getOriginalFilename();
        String extension = FilenameUtils.getExtension(originalFilename);
        String storedName = UUID.randomUUID().toString() + "." + extension;

        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String relativePath = String.format("/%s/%s/%s", entityType, datePath, storedName);
        Path basePath = Paths.get(uploadPath).toAbsolutePath().normalize();
        Path targetPath = basePath.resolve(Paths.get(entityType, datePath, storedName)).normalize();

        try {
            Files.createDirectories(targetPath.getParent());
            file.transferTo(targetPath.toFile());

            FileRecord fileRecord = new FileRecord();
            fileRecord.setOriginalName(originalFilename);
            fileRecord.setStoredName(storedName);
            fileRecord.setFilePath(relativePath);
            fileRecord.setFileSize(file.getSize());
            fileRecord.setFileType(file.getContentType());
            fileRecord.setEntityType(entityType);
            fileRecord.setEntityId(entityId);
            fileRecord.setUploaderId(uploaderId);
            fileRecordRepository.save(fileRecord);

            return relativePath;
        } catch (IOException e) {
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }
    }

    @Transactional
    public void deleteFile(String filePath) {
        try {
            String cleanedPath = filePath.startsWith("/") ? filePath.substring(1) : filePath;
            Path path = Paths.get(uploadPath).toAbsolutePath().normalize().resolve(cleanedPath).normalize();
            Files.deleteIfExists(path);
            fileRecordRepository.deleteByFilePath(filePath);
        } catch (IOException e) {
            throw new BusinessException("文件删除失败: " + e.getMessage());
        }
    }

    public FileRecord getFileRecord(String filePath) {
        return fileRecordRepository.findByFilePath(filePath)
                .orElseThrow(() -> new BusinessException("文件不存在"));
    }

    public List<FileRecord> getFilesByEntity(String entityType, Long entityId) {
        return fileRecordRepository.findByEntityTypeAndEntityId(entityType, entityId);
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }

        if (file.getSize() > maxSize) {
            throw new BusinessException("文件大小超过限制");
        }

        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        List<String> allowedTypeList = Arrays.asList(allowedTypes.split(","));
        if (!allowedTypeList.contains(extension.toLowerCase())) {
            throw new BusinessException("不支持的文件类型");
        }
    }
}
