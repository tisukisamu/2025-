package com.example.backend.controller;

import com.example.backend.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FileController {

    private final FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "category", required = false, defaultValue = "common") String category) {
        String relativePath = fileStorageService.storeImage(file, category);
        Map<String, Object> data = new HashMap<>();
        data.put("path", relativePath);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "上传成功");
        response.put("data", data);
        return ResponseEntity.ok(response);
    }
}
