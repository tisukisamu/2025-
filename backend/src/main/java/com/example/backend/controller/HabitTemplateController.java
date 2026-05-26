package com.example.backend.controller;

import com.example.backend.dto.CreateHabitFromTemplateRequest;
import com.example.backend.dto.HabitDTO;
import com.example.backend.dto.HabitTemplateDTO;
import com.example.backend.service.HabitTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/templates/habits")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HabitTemplateController {

    private final HabitTemplateService habitTemplateService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getTemplates(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false) String repeatType
    ) {
        List<HabitTemplateDTO> templates = habitTemplateService.getTemplates(keyword, categoryName, repeatType);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", templates);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{templateId}/create")
    public ResponseEntity<Map<String, Object>> createFromTemplate(
            @PathVariable Long templateId,
            @RequestBody(required = false) CreateHabitFromTemplateRequest request) {
        CreateHabitFromTemplateRequest req = request != null ? request : new CreateHabitFromTemplateRequest();
        HabitDTO habit = habitTemplateService.createHabitFromTemplate(templateId, req);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "创建成功");
        response.put("data", habit);
        return ResponseEntity.ok(response);
    }
}
