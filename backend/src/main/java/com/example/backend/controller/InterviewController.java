package com.example.backend.controller;

import com.example.backend.dto.InterviewDTO;
import com.example.backend.entity.Interview;
import com.example.backend.service.InterviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/interviews")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InterviewController {

    private final InterviewService interviewService;

    @GetMapping("/application/{applicationId}")
    public ResponseEntity<Map<String, Object>> getInterviewsByApplicationId(@PathVariable Long applicationId) {
        List<Interview> interviews = interviewService.getInterviewsByApplicationId(applicationId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", interviews);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getInterviewById(@PathVariable Long id) {
        Interview interview = interviewService.getInterviewById(id);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", interview);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createInterview(@Valid @RequestBody InterviewDTO interviewDTO) {
        Interview interview = interviewService.createInterview(interviewDTO);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "面试创建成功");
        response.put("data", interview);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateInterview(
            @PathVariable Long id,
            @Valid @RequestBody InterviewDTO interviewDTO) {
        Interview interview = interviewService.updateInterview(id, interviewDTO);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "更新成功");
        response.put("data", interview);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/result")
    public ResponseEntity<Map<String, Object>> updateInterviewResult(
            @PathVariable Long id,
            @RequestParam String result,
            @RequestParam(required = false) String feedback) {
        interviewService.updateInterviewResult(id, Interview.Result.valueOf(result), feedback);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "面试结果更新成功");
        return ResponseEntity.ok(response);
    }
}
