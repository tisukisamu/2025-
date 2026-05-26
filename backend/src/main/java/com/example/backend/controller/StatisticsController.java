package com.example.backend.controller;

import com.example.backend.dto.response.StatisticsDTO;
import com.example.backend.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {
    
    private final StatisticsService statisticsService;
    
    @GetMapping("/courses")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StatisticsDTO.CourseStatistics> getCourseStatistics() {
        StatisticsDTO.CourseStatistics statistics = statisticsService.getCourseStatistics();
        return ResponseEntity.ok(statistics);
    }
    
    @GetMapping("/finance")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StatisticsDTO.FinanceStatistics> getFinanceStatistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        
        if (start == null) {
            start = LocalDate.now().minusMonths(1);
        }
        if (end == null) {
            end = LocalDate.now();
        }
        
        StatisticsDTO.FinanceStatistics statistics = statisticsService.getFinanceStatistics(start, end);
        return ResponseEntity.ok(statistics);
    }
    
    @GetMapping("/students")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<StatisticsDTO.StudentStatistics> getStudentStatistics() {
        StatisticsDTO.StudentStatistics statistics = statisticsService.getStudentStatistics();
        return ResponseEntity.ok(statistics);
    }
    
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStatistics() {
        Map<String, Object> statistics = statisticsService.getDashboardStatistics();
        return ResponseEntity.ok(statistics);
    }
}
