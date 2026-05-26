package com.example.backend.service;

import com.example.backend.entity.User;
import com.example.backend.dto.UserProfileUpdateDTO;
import com.example.backend.exception.BusinessException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", id));
    }

    @Transactional
    public User create(User user) {
        // 检查邮箱是否已存在
        if (user.getEmail() != null && userRepository.existsByEmail(user.getEmail())) {
            throw new BusinessException(409, "邮箱已被注册: " + user.getEmail());
        }
        return userRepository.save(user);
    }

    @Transactional
    public User update(Long id, User user) {
        User existingUser = findById(id);
        
        // 检查邮箱是否被其他用户使用
        if (user.getEmail() != null && !user.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.existsByEmail(user.getEmail())) {
                throw new BusinessException(409, "邮箱已被其他用户使用: " + user.getEmail());
            }
        }
        
        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());
        existingUser.setAge(user.getAge());
        existingUser.setAvatarUrl(user.getAvatarUrl());
        return userRepository.save(existingUser);
    }

    @Transactional
    public User updateProfile(Long userId, UserProfileUpdateDTO dto) {
        User existingUser = findById(userId);
        if (dto.getEmail() != null && !dto.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.existsByEmail(dto.getEmail())) {
                throw new BusinessException(409, "邮箱已被其他用户使用: " + dto.getEmail());
            }
        }
        existingUser.setName(dto.getName());
        existingUser.setEmail(dto.getEmail());
        existingUser.setAge(dto.getAge());
        existingUser.setAvatarUrl(dto.getAvatarUrl());
        return userRepository.save(existingUser);
    }

    @Transactional
    public void delete(Long id) {
        // 先检查用户是否存在
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("用户", "id", id);
        }
        userRepository.deleteById(id);
    }
}
