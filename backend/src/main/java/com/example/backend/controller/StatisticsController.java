package com.example.backend.controller;

import com.example.backend.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getSystemOverview() {
        Map<String, Object> overview = statisticsService.getSystemOverview();
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", overview);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<Map<String, Object>> getCompanyOverview(@PathVariable Long companyId) {
        Map<String, Object> overview = statisticsService.getCompanyOverview(companyId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", overview);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/recruitment")
    public ResponseEntity<Map<String, Object>> getRecruitmentTrend(
            @RequestParam Long companyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        Map<String, Object> trend = statisticsService.getRecruitmentTrend(companyId, start, end);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", trend);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/talent")
    public ResponseEntity<Map<String, Object>> getTalentAnalysis(@RequestParam Long companyId) {
        Map<String, Object> analysis = statisticsService.getTalentAnalysis(companyId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", analysis);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/system")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAdminSystemStats() {
        Map<String, Object> stats = statisticsService.getSystemOverview();
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", stats);
        return ResponseEntity.ok(response);
    }
}
