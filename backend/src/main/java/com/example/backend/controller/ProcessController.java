package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.dto.ProcessResponse;
import com.example.backend.dto.ProcessUpdateRequest;
import com.example.backend.security.UserDetailsImpl;
import com.example.backend.service.ProcessService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/process")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProcessController {

    private final ProcessService processService;

    @GetMapping("/appointment/{appointmentId}")
    public Result<List<ProcessResponse>> getProcessByAppointment(@PathVariable Long appointmentId) {
        List<ProcessResponse> process = processService.getProcessByAppointmentId(appointmentId);
        return Result.success(process);
    }

    @PutMapping("/appointment/{appointmentId}")
    public Result<ProcessResponse> updateProcess(
            @PathVariable Long appointmentId,
            @Valid @RequestBody ProcessUpdateRequest request) {
        Long operatorId = getCurrentUserId();
        ProcessResponse process = processService.updateProcess(appointmentId, request, operatorId);
        return Result.success("更新成功", process);
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
