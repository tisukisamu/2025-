package com.example.backend.controller;

import com.example.backend.entity.Job;
import com.example.backend.security.UserDetailsImpl;
import com.example.backend.service.JobFavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/favorites/jobs")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class JobFavoriteController {

    private final JobFavoriteService jobFavoriteService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getMyFavorites(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<Job> jobs = jobFavoriteService.getMyFavoriteJobs(userDetails.getId());
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", jobs);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ids")
    public ResponseEntity<Map<String, Object>> getMyFavoriteIds(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Set<Long> ids = jobFavoriteService.getMyFavoriteJobIds(userDetails.getId());
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", ids);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{jobId}")
    public ResponseEntity<Map<String, Object>> addFavorite(
            @PathVariable Long jobId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        jobFavoriteService.addFavorite(userDetails.getId(), jobId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "收藏成功");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{jobId}")
    public ResponseEntity<Map<String, Object>> removeFavorite(
            @PathVariable Long jobId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        jobFavoriteService.removeFavorite(userDetails.getId(), jobId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "已取消收藏");
        return ResponseEntity.ok(response);
    }
}
