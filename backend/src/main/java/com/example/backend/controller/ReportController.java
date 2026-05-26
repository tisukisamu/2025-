package com.example.backend.controller;

import com.example.backend.dto.ReportDTO;
import com.example.backend.dto.response.ApiResponse;
import com.example.backend.dto.response.PageResponse;
import com.example.backend.entity.Report;
import com.example.backend.security.UserDetailsImpl;
import com.example.backend.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReportController {

    private final ReportService reportService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<ReportDTO>>> getReports(
            @RequestParam(required = false) Report.ReportStatus status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<ReportDTO> reports = reportService.getReports(status, page, size);
        return ResponseEntity.ok(ApiResponse.success(reports));
    }

    @GetMapping("/mine")
    public ResponseEntity<ApiResponse<PageResponse<ReportDTO>>> getMyReports(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<ReportDTO> reports = reportService.getMyReports(userDetails.getId(), page, size);
        return ResponseEntity.ok(ApiResponse.success(reports));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ReportDTO>> createReport(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam Long productId,
            @RequestParam Report.ReportType type,
            @RequestParam(required = false) String reason) {
        ReportDTO report = reportService.createReport(userDetails.getId(), productId, type, reason);
        return ResponseEntity.ok(ApiResponse.success("举报成功，我们会尽快处理", report));
    }

    @PutMapping("/{id}/handle")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ReportDTO>> handleReport(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam Report.ReportStatus status,
            @RequestParam(required = false) String result) {
        ReportDTO report = reportService.handleReport(id, userDetails.getId(), status, result);
        return ResponseEntity.ok(ApiResponse.success("处理完成", report));
    }
}
