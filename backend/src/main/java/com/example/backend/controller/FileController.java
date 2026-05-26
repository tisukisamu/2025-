package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.entity.FileRecord;
import com.example.backend.security.UserDetailsImpl;
import com.example.backend.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FileController {

    private final FileService fileService;

    @Value("${file.upload.path:./upload}")
    private String uploadPath;

    @PostMapping("/upload")
    public Result<Map<String, String>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) Long entityId) {
        
        Long uploaderId = getCurrentUserId();
        String filePath = fileService.uploadFile(file, entityType, entityId, uploaderId);
        
        Map<String, String> result = new HashMap<>();
        result.put("url", "/upload" + filePath);
        result.put("path", filePath);
        
        return Result.success("上传成功", result);
    }

    @DeleteMapping
    public Result<Void> deleteFile(@RequestParam String filePath) {
        fileService.deleteFile(filePath);
        return Result.success("删除成功", null);
    }

    @GetMapping("/entity/{entityType}/{entityId}")
    public Result<List<FileRecord>> getFilesByEntity(
            @PathVariable String entityType,
            @PathVariable Long entityId) {
        List<FileRecord> files = fileService.getFilesByEntity(entityType, entityId);
        return Result.success(files);
    }

    @GetMapping("/**")
    public ResponseEntity<Resource> downloadFile(HttpServletRequest request) {
        try {
            String requestUri = request.getRequestURI();
            String prefix = "/api/files/";
            int prefixIndex = requestUri.indexOf(prefix);
            if (prefixIndex < 0) {
                return ResponseEntity.notFound().build();
            }
            String filePath = requestUri.substring(prefixIndex + prefix.length());
            if (filePath.isBlank()) {
                return ResponseEntity.notFound().build();
            }

            Path basePath = Paths.get(uploadPath).toAbsolutePath().normalize();
            Path targetPath = basePath.resolve(filePath).normalize();
            if (!targetPath.startsWith(basePath)) {
                return ResponseEntity.badRequest().build();
            }
            Resource resource = new UrlResource(targetPath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                String contentType = Files.probeContentType(targetPath);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetailsImpl userDetails) {
            return userDetails.getId();
        }
        return Long.parseLong(authentication.getName());
    }
}
