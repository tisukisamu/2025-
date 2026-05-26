package com.example.backend.controller;

import com.example.backend.dto.response.AuthResponse;
import com.example.backend.dto.request.LoginRequest;
import com.example.backend.dto.request.RegisterRequest;
import com.example.backend.entity.User;
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

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            
            if (userDetails.getStatus() == User.Status.INACTIVE) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 403);
                response.put("message", "账户已被禁用，请联系管理员");
                return ResponseEntity.status(403).body(response);
            }

            if (userDetails.getStatus() == User.Status.BANNED) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 403);
                response.put("message", "账户已被封禁，请联系管理员");
                return ResponseEntity.status(403).body(response);
            }

            String jwt = jwtUtil.generateToken(userDetails.getUsername(), userDetails.getRole().name());

            AuthResponse authResponse = AuthResponse.builder()
                    .token(jwt)
                    .user(com.example.backend.dto.response.UserDTO.builder()
                            .id(userDetails.getId())
                            .username(userDetails.getUsername())
                            .email(userDetails.getEmail())
                            .realName(userDetails.getRealName())
                            .role(userDetails.getRole())
                            .status(userDetails.getStatus())
                            .build())
                    .build();

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "登录成功");
            response.put("data", authResponse);
            return ResponseEntity.ok(response);

        } catch (DisabledException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 403);
            response.put("message", "账户已被禁用");
            return ResponseEntity.status(403).body(response);
        } catch (BadCredentialsException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 401);
            response.put("message", "用户名或密码错误");
            return ResponseEntity.status(401).body(response);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            User user = authService.register(registerRequest);
            
            String jwt = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
            
            AuthResponse authResponse = AuthResponse.builder()
                    .token(jwt)
                    .user(com.example.backend.dto.response.UserDTO.builder()
                            .id(user.getId())
                            .username(user.getUsername())
                            .email(user.getEmail())
                            .realName(user.getRealName())
                            .phone(user.getPhone())
                            .role(user.getRole())
                            .status(user.getStatus())
                            .build())
                    .build();

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "注册成功");
            response.put("data", authResponse);
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 400);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        User user = authService.getUserById(userDetails.getId());

        AuthResponse authResponse = AuthResponse.builder()
                .user(com.example.backend.dto.response.UserDTO.builder()
                        .id(userDetails.getId())
                        .username(userDetails.getUsername())
                        .email(userDetails.getEmail())
                        .realName(userDetails.getRealName())
                        .avatar(user.getAvatar())
                        .role(userDetails.getRole())
                        .status(userDetails.getStatus())
                        .build())
                .build();

        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", authResponse);
        return ResponseEntity.ok(response);
    }
}
