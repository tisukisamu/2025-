package com.club.fund.controller;

import com.club.fund.common.Result;
import com.club.fund.dto.request.PasswordChangeRequest;
import com.club.fund.dto.request.UserUpdateRequest;
import com.club.fund.dto.response.UserResponse;
import com.club.fund.service.AuthService;
import com.club.fund.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;

    @GetMapping("/info")
    public Result<UserResponse> getUserInfo() {
        Long userId = SecurityUtil.getCurrentUserId();
        return Result.success(authService.getCurrentUser(userId));
    }

    @PutMapping("/profile")
    public Result<UserResponse> updateProfile(@Valid @RequestBody UserUpdateRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        return Result.success(authService.updateProfile(userId, request));
    }

    @PutMapping("/password")
    public Result<Void> changePassword(@Valid @RequestBody PasswordChangeRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        authService.changePassword(userId, request);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<Page<UserResponse>> getUserList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return Result.success(authService.getUserList(pageable));
    }

    @PutMapping("/{id}/status")
    public Result<UserResponse> updateUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        return Result.success(authService.updateUserStatus(id, status));
    }

    @PutMapping("/{id}")
    public Result<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {
        return Result.success(authService.updateUser(id, request));
    }
}
