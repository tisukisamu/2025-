package com.example.backend.controller;

import com.example.backend.dto.AchievementProgressDTO;
import com.example.backend.service.AchievementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/achievements")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AchievementController {

    private final AchievementService achievementService;

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getMyAchievements(
            @RequestParam(defaultValue = "false") Boolean earnedOnly,
            @RequestParam(required = false) String conditionType,
            @RequestParam(required = false) String sortBy
    ) {
        List<AchievementProgressDTO> list = achievementService.getMyAchievements(earnedOnly, conditionType, sortBy);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", list);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/summary")
    public ResponseEntity<Map<String, Object>> getSummary() {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", achievementService.getMySummary());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/evaluate")
    public ResponseEntity<Map<String, Object>> evaluate() {
        achievementService.evaluateForCurrentUser();
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", true);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ranking")
    public ResponseEntity<Map<String, Object>> getRanking(@RequestParam(defaultValue = "20") Integer limit) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", achievementService.getAchievementRanking(limit));
        return ResponseEntity.ok(response);
    }
}
