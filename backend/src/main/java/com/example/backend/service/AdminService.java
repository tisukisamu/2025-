package com.example.backend.service;

import com.example.backend.dto.UserDTO;
import com.example.backend.entity.User;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    public List<UserDTO> findAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDTO updateUserStatus(Long id, User.Status status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", id));
        user.setStatus(status);
        return UserDTO.fromEntity(userRepository.save(user));
    }

    @Transactional
    public UserDTO updateUserRole(Long id, User.Role role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", id));
        user.setRole(role);
        return UserDTO.fromEntity(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("用户", "id", id);
        }
        userRepository.deleteById(id);
    }

    public Map<String, Object> getDashboardStats() {
        List<User> users = userRepository.findAll();
        
        long totalUsers = users.size();
        long activeUsers = users.stream().filter(u -> u.getStatus() == User.Status.ACTIVE).count();
        long inactiveUsers = users.stream().filter(u -> u.getStatus() == User.Status.INACTIVE).count();
        long adminCount = users.stream().filter(u -> u.getRole() == User.Role.ADMIN).count();
        long teacherCount = users.stream().filter(u -> u.getRole() == User.Role.TEACHER).count();
        long studentCount = users.stream().filter(u -> u.getRole() == User.Role.STUDENT).count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", totalUsers);
        stats.put("activeUsers", activeUsers);
        stats.put("inactiveUsers", inactiveUsers);
        stats.put("adminCount", adminCount);
        stats.put("teacherCount", teacherCount);
        stats.put("studentCount", studentCount);
        
        return stats;
    }
}
