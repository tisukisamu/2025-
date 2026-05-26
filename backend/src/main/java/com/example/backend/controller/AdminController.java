package com.example.backend.controller;

import com.example.backend.dto.UserDTO;
import com.example.backend.entity.User;
import com.example.backend.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        List<UserDTO> users = adminService.findAllUsers();
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", users);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/users/{id}/status")
    public ResponseEntity<Map<String, Object>> updateUserStatus(
            @PathVariable Long id,
            @RequestParam User.Status status) {
        UserDTO user = adminService.updateUserStatus(id, status);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "状态更新成功");
        response.put("data", user);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<Map<String, Object>> updateUserRole(
            @PathVariable Long id,
            @RequestParam User.Role role) {
        UserDTO user = adminService.updateUserRole(id, role);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "角色更新成功");
        response.put("data", user);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "用户删除成功");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = adminService.getDashboardStats();
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", stats);
        return ResponseEntity.ok(response);
    }
}
