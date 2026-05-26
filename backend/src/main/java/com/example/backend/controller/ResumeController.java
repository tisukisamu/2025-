package com.example.backend.controller;

import com.example.backend.dto.ResumeDTO;
import com.example.backend.entity.Resume;
import com.example.backend.security.UserDetailsImpl;
import com.example.backend.service.ResumeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ResumeController {

    private final ResumeService resumeService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getMyResumes(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<Resume> resumes = resumeService.getResumesByUserId(userDetails.getId());
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", resumes);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getResumeById(@PathVariable Long id) {
        Resume resume = resumeService.getResumeById(id);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", resume);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createResume(
            @Valid @RequestBody ResumeDTO resumeDTO,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Resume resume = resumeService.createResume(resumeDTO, userDetails.getId());
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "简历创建成功");
        response.put("data", resume);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateResume(
            @PathVariable Long id,
            @Valid @RequestBody ResumeDTO resumeDTO) {
        Resume resume = resumeService.updateResume(id, resumeDTO);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "更新成功");
        response.put("data", resume);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteResume(@PathVariable Long id) {
        resumeService.deleteResume(id);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "删除成功");
        return ResponseEntity.ok(response);
    }
}
