package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.dto.AppointmentRequest;
import com.example.backend.dto.AppointmentResponse;
import com.example.backend.dto.PageResponse;
import com.example.backend.dto.ProcessResponse;
import com.example.backend.security.UserDetailsImpl;
import com.example.backend.service.AppointmentService;
import com.example.backend.service.ProcessService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final ProcessService processService;

    @PostMapping
    public Result<AppointmentResponse> createAppointment(@Valid @RequestBody AppointmentRequest request) {
        Long userId = getCurrentUserId();
        AppointmentResponse appointment = appointmentService.createAppointment(userId, request);
        return Result.success("预约成功", appointment);
    }

    @GetMapping
    public Result<PageResponse<AppointmentResponse>> getUserAppointments(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Long userId = getCurrentUserId();
        PageResponse<AppointmentResponse> result = appointmentService.getUserAppointments(userId, status, pageNum, pageSize);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<AppointmentResponse> getAppointmentById(@PathVariable Long id) {
        AppointmentResponse appointment = appointmentService.getAppointmentById(id);
        return Result.success(appointment);
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam String status) {
        Long operatorId = getCurrentUserId();
        appointmentService.updateStatus(id, status, operatorId);
        return Result.success("状态更新成功", null);
    }

    @PutMapping("/{id}/cancel")
    public Result<Void> cancelAppointment(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        appointmentService.cancelAppointment(id, userId);
        return Result.success("取消成功", null);
    }

    @GetMapping("/{id}/process")
    public Result<List<ProcessResponse>> getProcess(@PathVariable Long id) {
        List<ProcessResponse> process = processService.getProcessByAppointmentId(id);
        return Result.success(process);
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
