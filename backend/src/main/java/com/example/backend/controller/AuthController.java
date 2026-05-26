package com.example.backend.controller;

import com.example.backend.dto.AuthResponse;
import com.example.backend.dto.LoginRequest;
import com.example.backend.dto.RegisterRequest;
import com.example.backend.dto.response.ApiResponse;
import com.example.backend.entity.User;
import com.example.backend.exception.BusinessException;
import com.example.backend.security.JwtUtil;
import com.example.backend.security.UserDetailsImpl;
import com.example.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            if (userDetails.getStatus() == User.Status.BANNED) {
                throw new BusinessException(403, "账户已被禁用，请联系管理员");
            }

            String jwt = jwtUtil.generateToken(userDetails.getUsername(), userDetails.getRole().name());

            AuthResponse authResponse = AuthResponse.builder()
                    .token(jwt)
                    .id(userDetails.getId())
                    .username(userDetails.getUsername())
                    .studentId(userDetails.getStudentId())
                    .realName(userDetails.getRealName())
                    .avatar(userDetails.getAvatar())
                    .role(userDetails.getRole())
                    .status(userDetails.getStatus())
                    .build();

            return ResponseEntity.ok(ApiResponse.success("登录成功", authResponse));

        } catch (DisabledException e) {
            throw new BusinessException(403, "账户已被禁用");
        } catch (BadCredentialsException e) {
            throw new BusinessException(401, "用户名或密码错误");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        AuthResponse authResponse = authService.register(registerRequest);
        return ResponseEntity.ok(ApiResponse.success("注册成功", authResponse));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(ApiResponse.success("退出成功", null));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthResponse>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl)) {
            throw new BusinessException(401, "未登录");
        }
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        AuthResponse authResponse = AuthResponse.builder()
                .id(userDetails.getId())
                .username(userDetails.getUsername())
                .studentId(userDetails.getStudentId())
                .realName(userDetails.getRealName())
                .avatar(userDetails.getAvatar())
                .role(userDetails.getRole())
                .status(userDetails.getStatus())
                .build();

        return ResponseEntity.ok(ApiResponse.success(authResponse));
    }
}
