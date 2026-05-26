package com.example.backend.controller;

import com.example.backend.dto.ApplicationDTO;
import com.example.backend.entity.Application;
import com.example.backend.security.UserDetailsImpl;
import com.example.backend.service.ApplicationService;
import com.example.backend.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final CompanyService companyService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getMyApplications(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<Application> applications = applicationService.getApplicationsByUserId(userDetails.getId());
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", applications);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getApplicationById(@PathVariable Long id) {
        Application application = applicationService.getApplicationById(id);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", application);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/received")
    public ResponseEntity<Map<String, Object>> getReceivedApplications(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long companyId = companyService.getCompanyByUserId(userDetails.getId()).getId();
        List<Application> applications = applicationService.getApplicationsByCompanyId(companyId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", applications);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createApplication(
            @Valid @RequestBody ApplicationDTO applicationDTO,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        applicationDTO.setUserId(userDetails.getId());
        Application application = applicationService.createApplication(applicationDTO);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "投递成功");
        response.put("data", application);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateApplicationStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        applicationService.updateApplicationStatus(id, Application.Status.valueOf(status));
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "状态更新成功");
        return ResponseEntity.ok(response);
    }
}
