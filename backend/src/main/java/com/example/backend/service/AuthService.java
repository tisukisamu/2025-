package com.example.backend.service;

import com.example.backend.dto.RegisterRequest;
import com.example.backend.entity.User;
import com.example.backend.exception.BusinessException;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User register(RegisterRequest request) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException(409, "用户名已存在: " + request.getUsername());
        }

        // 检查邮箱是否已存在
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(409, "邮箱已被注册: " + request.getEmail());
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setAge(request.getAge());
        user.setRole(User.Role.USER); // 默认普通用户
        user.setStatus(User.Status.ACTIVE);

        return userRepository.save(user);
    }
}
