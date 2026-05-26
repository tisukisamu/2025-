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

    @PostMapping("/avatar")
    public ResponseEntity<Map<String, Object>> uploadAvatar(
            @RequestParam("file") MultipartFile file) {
        String url = fileUploadService.uploadAvatar(file);
        
        Map<String, Object> data = new HashMap<>();
        data.put("url", url);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "头像上传成功");
        response.put("data", data);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/habit")
    public ResponseEntity<Map<String, Object>> uploadHabitIcon(
            @RequestParam("file") MultipartFile file) {
        String url = fileUploadService.uploadHabitIcon(file);
        
        Map<String, Object> data = new HashMap<>();
        data.put("url", url);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "习惯图标上传成功");
        response.put("data", data);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/image")
    public ResponseEntity<Map<String, Object>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "category", defaultValue = "common") String category) {
        String url = fileUploadService.uploadImage(file, category);
        
        Map<String, Object> data = new HashMap<>();
        data.put("url", url);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "图片上传成功");
        response.put("data", data);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<Map<String, Object>> deleteFile(
            @RequestParam("path") String path) {
        fileUploadService.deleteFile(path);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "文件删除成功");
        return ResponseEntity.ok(response);
    }
}
