package com.example.backend.controller;

import com.example.backend.dto.CalendarDTO;
import com.example.backend.dto.CheckDTO;
import com.example.backend.dto.CheckRequest;
import com.example.backend.dto.TodayOverviewDTO;
import com.example.backend.service.CheckService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/checks")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CheckController {

    private final CheckService checkService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> checkIn(@Valid @RequestBody CheckRequest request) {
        CheckDTO record = checkService.checkIn(request.getHabitId(), request.getNote());
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "打卡成功");
        response.put("data", record);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{habitId}")
    public ResponseEntity<Map<String, Object>> cancelCheck(
            @PathVariable Long habitId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        checkService.cancelCheck(habitId, date);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "取消打卡成功");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/today")
    public ResponseEntity<Map<String, Object>> getTodayChecks() {
        List<CheckDTO> checks = checkService.getTodayChecks();
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", checks);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getTodayOverview() {
        TodayOverviewDTO overview = checkService.getTodayOverview();
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", overview);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/calendar")
    public ResponseEntity<Map<String, Object>> getCalendar(
            @RequestParam String month,
            @RequestParam(required = false) Long habitId) {
        CalendarDTO calendar = checkService.getCalendar(month, habitId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", calendar);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<Map<String, Object>> getHistory(
            @RequestParam(required = false) Long habitId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<CheckDTO> history = checkService.getHistory(habitId, page, size);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", history.getContent());
        response.put("totalElements", history.getTotalElements());
        response.put("totalPages", history.getTotalPages());
        response.put("currentPage", history.getNumber());
        return ResponseEntity.ok(response);
    }
}
