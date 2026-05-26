package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.dto.AppointmentResponse;
import com.example.backend.dto.PageResponse;
import com.example.backend.dto.ProcessResponse;
import com.example.backend.dto.ProcessUpdateRequest;
import com.example.backend.security.UserDetailsImpl;
import com.example.backend.service.AppointmentService;
import com.example.backend.service.ProcessService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/service")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SERVICE', 'ADMIN')")
@CrossOrigin(origins = "*")
public class ServiceController {

    private final AppointmentService appointmentService;
    private final ProcessService processService;

    @GetMapping("/appointments")
    public Result<PageResponse<AppointmentResponse>> getAllAppointments(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResponse<AppointmentResponse> result = appointmentService.getAllAppointments(status, pageNum, pageSize);
        return Result.success(result);
    }

    @GetMapping("/appointments/{id}")
    public Result<AppointmentResponse> getAppointmentById(@PathVariable Long id) {
        AppointmentResponse appointment = appointmentService.getAppointmentById(id);
        return Result.success(appointment);
    }

    @PutMapping("/appointments/{id}/status")
    public Result<Void> updateAppointmentStatus(@PathVariable Long id, @RequestParam String status) {
        Long operatorId = getCurrentUserId();
        appointmentService.updateStatus(id, status, operatorId);
        return Result.success("状态更新成功", null);
    }

    @GetMapping("/appointments/{id}/process")
    public Result<List<ProcessResponse>> getAppointmentProcess(@PathVariable Long id) {
        List<ProcessResponse> process = processService.getProcessByAppointmentId(id);
        return Result.success(process);
    }

    @PutMapping("/appointments/{id}/process")
    public Result<ProcessResponse> updateProcess(
            @PathVariable Long id,
            @Valid @RequestBody ProcessUpdateRequest request) {
        Long operatorId = getCurrentUserId();
        ProcessResponse response = processService.updateProcess(id, request, operatorId);
        return Result.success("流程更新成功", response);
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetailsImpl userDetails) {
            return userDetails.getId();
        }
        return Long.parseLong(authentication.getName());
    }
}
