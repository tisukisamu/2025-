package com.example.backend.controller;

import com.example.backend.dto.CheckinRankingDTO;
import com.example.backend.dto.StatisticsDTO;
import com.example.backend.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/today")
    public ResponseEntity<Map<String, Object>> getTodayOverview() {
        return ResponseEntity.ok(Map.of(
            "code", 200,
            "message", "success",
            "data", "请使用 /api/checks/overview 接口"
        ));
    }

    @GetMapping("/habit/{id}")
    public ResponseEntity<Map<String, Object>> getHabitStatistics(@PathVariable Long id) {
        StatisticsDTO stats = statisticsService.getHabitStatistics(id);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", stats);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllStatistics() {
        List<StatisticsDTO> stats = statisticsService.getAllStatistics();
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", stats);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/trend")
    public ResponseEntity<Map<String, Object>> getTrend(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        StatisticsService.TrendData trend = statisticsService.getTrend(start, end);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", trend);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/heatmap")
    public ResponseEntity<Map<String, Object>> getHeatmap(@RequestParam int year) {
        StatisticsService.HeatmapData heatmap = statisticsService.getHeatmap(year);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", heatmap);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ranking")
    public ResponseEntity<Map<String, Object>> getCheckinRanking(
            @RequestParam(defaultValue = "10") Integer limit
    ) {
        List<CheckinRankingDTO> ranking = statisticsService.getCheckinRanking(limit);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", ranking);
        return ResponseEntity.ok(response);
    }
}
