package com.example.backend.controller;

import com.example.backend.dto.ReminderDTO;
import com.example.backend.service.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reminders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReminderController {

    private final ReminderService reminderService;

    @GetMapping("/today")
    public ResponseEntity<Map<String, Object>> getTodayReminders(@RequestParam(defaultValue = "false") Boolean onlyPending) {
        List<ReminderDTO> list = reminderService.getTodayReminders(onlyPending);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", list);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/today/summary")
    public ResponseEntity<Map<String, Object>> getTodaySummary() {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", reminderService.getTodaySummary());
        return ResponseEntity.ok(response);
    }
}
