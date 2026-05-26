package com.example.backend.controller;

import com.example.backend.dto.ComplaintSuggestionDTO;
import com.example.backend.entity.ComplaintSuggestion;
import com.example.backend.security.UserDetailsImpl;
import com.example.backend.service.ComplaintSuggestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ComplaintSuggestionController {

    private final ComplaintSuggestionService complaintSuggestionService;

    @PostMapping("/complaints")
    public ResponseEntity<Map<String, Object>> createComplaint(
            @Valid @RequestBody ComplaintSuggestionDTO dto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        ComplaintSuggestion complaint = complaintSuggestionService.createComplaint(dto, userDetails.getId());
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "提交成功");
        response.put("data", complaint);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/complaints")
    public ResponseEntity<Map<String, Object>> getMyComplaints(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<ComplaintSuggestion> complaints = complaintSuggestionService.getComplaintsByUserId(userDetails.getId());
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", complaints);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/complaints")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAllComplaints() {
        List<ComplaintSuggestion> complaints = complaintSuggestionService.getAllComplaints();
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", complaints);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/admin/complaints/{id}/handle")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> handleComplaint(
            @PathVariable Long id,
            @RequestParam String result,
            @RequestParam String status,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        ComplaintSuggestion complaint = complaintSuggestionService.handleComplaint(
                id, 
                userDetails.getId(), 
                result, 
                ComplaintSuggestion.Status.valueOf(status)
        );
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "处理成功");
        response.put("data", complaint);
        return ResponseEntity.ok(response);
    }
}
