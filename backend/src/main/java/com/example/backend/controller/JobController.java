package com.example.backend.controller;

import com.example.backend.dto.JobDTO;
import com.example.backend.entity.Job;
import com.example.backend.security.UserDetailsImpl;
import com.example.backend.service.CompanyService;
import com.example.backend.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class JobController {

    private final JobService jobService;
    private final CompanyService companyService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Job> jobs = jobService.searchJobs(null, null, null, null, pageable);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", jobs);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getJobById(@PathVariable Long id) {
        Job job = jobService.getJobById(id);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", job);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchJobs(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String education,
            @RequestParam(required = false) String experience,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Job> jobs = jobService.searchJobs(title, location, education, experience, pageable);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", jobs);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<Map<String, Object>> getJobsByCompanyId(@PathVariable Long companyId) {
        List<Job> jobs = jobService.getJobsByCompanyId(companyId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", jobs);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    public ResponseEntity<Map<String, Object>> getMyJobs(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long companyId = companyService.getCompanyByUserId(userDetails.getId()).getId();
        List<Job> jobs = jobService.getJobsByCompanyId(companyId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", jobs);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createJob(
            @Valid @RequestBody JobDTO jobDTO,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long companyId = companyService.getCompanyByUserId(userDetails.getId()).getId();
        Job job = jobService.createJob(jobDTO, companyId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "职位发布成功");
        response.put("data", job);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateJob(
            @PathVariable Long id,
            @Valid @RequestBody JobDTO jobDTO) {
        Job job = jobService.updateJob(id, jobDTO);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "更新成功");
        response.put("data", job);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/close")
    public ResponseEntity<Map<String, Object>> closeJob(@PathVariable Long id) {
        jobService.closeJob(id);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "职位已下架");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteJob(@PathVariable Long id) {
        jobService.deleteJob(id);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "删除成功");
        return ResponseEntity.ok(response);
    }
}
