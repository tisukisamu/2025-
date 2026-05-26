package com.example.backend.controller;

import com.example.backend.dto.CategoryDTO;
import com.example.backend.dto.CreateHabitRequest;
import com.example.backend.dto.HabitDTO;
import com.example.backend.dto.UpdateHabitRequest;
import com.example.backend.service.CategoryService;
import com.example.backend.service.HabitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/habits")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HabitController {

    private final HabitService habitService;
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getHabits(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String status) {
        List<HabitDTO> habits = habitService.getHabits(categoryId, status);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", habits);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/today")
    public ResponseEntity<Map<String, Object>> getTodayHabits() {
        List<HabitDTO> habits = habitService.getTodayHabits();
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", habits);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getHabitById(@PathVariable Long id) {
        HabitDTO habit = habitService.getHabitById(id);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", habit);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createHabit(
            @Valid @RequestBody CreateHabitRequest request) {
        HabitDTO habit = habitService.createHabit(request);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "创建成功");
        response.put("data", habit);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateHabit(
            @PathVariable Long id,
            @Valid @RequestBody UpdateHabitRequest request) {
        HabitDTO habit = habitService.updateHabit(id, request);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "更新成功");
        response.put("data", habit);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteHabit(@PathVariable Long id) {
        habitService.deleteHabit(id);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "删除成功");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/pause")
    public ResponseEntity<Map<String, Object>> pauseHabit(@PathVariable Long id) {
        habitService.pauseHabit(id);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "习惯已暂停");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/resume")
    public ResponseEntity<Map<String, Object>> resumeHabit(@PathVariable Long id) {
        habitService.resumeHabit(id);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "习惯已恢复");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/batch/status")
    public ResponseEntity<Map<String, Object>> batchUpdateStatus(@RequestBody Map<String, Object> request) {
        List<?> rawIds = request.get("ids") instanceof List<?> ? (List<?>) request.get("ids") : List.of();
        List<Long> ids = rawIds.stream().map(v -> Long.valueOf(String.valueOf(v))).collect(Collectors.toList());
        String action = request.get("action") == null ? null : String.valueOf(request.get("action"));
        int updated = habitService.batchUpdateStatus(ids, action);

        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "批量操作成功");
        response.put("data", updated);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/categories")
    public ResponseEntity<Map<String, Object>> getCategories() {
        List<CategoryDTO> categories = categoryService.getCategories();
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", categories);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/categories")
    public ResponseEntity<Map<String, Object>> createCategory(
            @Valid @RequestBody CategoryDTO request) {
        CategoryDTO category = categoryService.createCategory(request);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "创建成功");
        response.put("data", category);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<Map<String, Object>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryDTO request) {
        CategoryDTO category = categoryService.updateCategory(id, request);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "更新成功");
        response.put("data", category);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Map<String, Object>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "删除成功");
        return ResponseEntity.ok(response);
    }
}
