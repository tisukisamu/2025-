package com.example.backend.controller;

import com.example.backend.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FileUploadController {
    
    private final FileUploadService fileUploadService;
    
    @PostMapping("/image")
    public ResponseEntity<Map<String, Object>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "courses") String type) {
        
        String imageUrl = fileUploadService.uploadImage(file, type);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "上传成功");
        response.put("data", Map.of("url", imageUrl));
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/avatar")
    public ResponseEntity<Map<String, Object>> uploadAvatar(
            @RequestParam("file") MultipartFile file) {
        
        String imageUrl = fileUploadService.uploadImage(file, "avatars");
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "上传成功");
        response.put("data", Map.of("url", imageUrl));
        
        return ResponseEntity.ok(response);
    }
}
