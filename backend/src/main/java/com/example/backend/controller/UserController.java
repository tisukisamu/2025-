package com.example.backend.controller;

import com.example.backend.dto.UserProfileUpdateDTO;
import com.example.backend.entity.User;
import com.example.backend.security.UserDetailsImpl;
import com.example.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        List<User> users = userService.findAll();
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", users);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id) {
        User user = userService.findById(id);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", user);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createUser(@Valid @RequestBody User user) {
        User createdUser = userService.create(user);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "创建成功");
        response.put("data", createdUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody User user) {
        User updatedUser = userService.update(id, user);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "更新成功");
        response.put("data", updatedUser);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me/profile")
    public ResponseEntity<Map<String, Object>> updateMyProfile(
            @Valid @RequestBody UserProfileUpdateDTO dto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User updatedUser = userService.updateProfile(userDetails.getId(), dto);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "个人资料更新成功");
        response.put("data", updatedUser);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "删除成功");
        return ResponseEntity.ok(response);
    }
}
