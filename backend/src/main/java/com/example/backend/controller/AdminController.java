package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.dto.AppointmentResponse;
import com.example.backend.dto.PageResponse;
import com.example.backend.dto.UserDTO;
import com.example.backend.entity.User;
import com.example.backend.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "*")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public Result<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = adminService.findAllUsers();
        return Result.success(users);
    }

    @PutMapping("/users/{id}/status")
    public Result<UserDTO> updateUserStatus(
            @PathVariable Long id,
            @RequestParam User.Status status) {
        UserDTO user = adminService.updateUserStatus(id, status);
        return Result.success("状态更新成功", user);
    }

    @PutMapping("/users/{id}/role")
    public Result<UserDTO> updateUserRole(
            @PathVariable Long id,
            @RequestParam User.Role role) {
        UserDTO user = adminService.updateUserRole(id, role);
        return Result.success("角色更新成功", user);
    }

    @DeleteMapping("/users/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return Result.success("用户删除成功", null);
    }

    @GetMapping("/appointments")
    public Result<PageResponse<AppointmentResponse>> getAllAppointments(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResponse<AppointmentResponse> result = adminService.getAllAppointments(status, pageNum, pageSize);
        return Result.success(result);
    }

    @GetMapping("/dashboard")
    public Result<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = adminService.getDashboardStats();
        return Result.success(stats);
    }
}
