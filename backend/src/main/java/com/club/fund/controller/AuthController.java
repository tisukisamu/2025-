package com.club.fund.controller;

import com.club.fund.common.Result;
import com.club.fund.dto.request.LoginRequest;
import com.club.fund.dto.request.UserCreateRequest;
import com.club.fund.dto.request.UserUpdateRequest;
import com.club.fund.dto.request.PasswordChangeRequest;
import com.club.fund.dto.response.LoginResponse;
import com.club.fund.dto.response.UserResponse;
import com.club.fund.service.AuthService;
import com.club.fund.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(authService.login(request));
    }

    @PostMapping("/register")
    public Result<UserResponse> register(@Valid @RequestBody UserCreateRequest request) {
        return Result.success(authService.register(request));
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success();
    }
}
