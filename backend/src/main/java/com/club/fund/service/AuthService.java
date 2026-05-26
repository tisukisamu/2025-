package com.club.fund.service;

import com.club.fund.common.Constants;
import com.club.fund.common.ErrorCode;
import com.club.fund.dto.request.LoginRequest;
import com.club.fund.dto.request.UserCreateRequest;
import com.club.fund.dto.request.UserUpdateRequest;
import com.club.fund.dto.request.PasswordChangeRequest;
import com.club.fund.dto.response.LoginResponse;
import com.club.fund.dto.response.RoleResponse;
import com.club.fund.dto.response.UserResponse;
import com.club.fund.entity.Role;
import com.club.fund.entity.User;
import com.club.fund.exception.AuthException;
import com.club.fund.exception.BusinessException;
import com.club.fund.repository.RoleRepository;
import com.club.fund.repository.UserRepository;
import com.club.fund.util.JwtUtil;
import com.club.fund.util.PermissionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AuthException(ErrorCode.USER_NOT_FOUND));

        if (user.getStatus() != 1) {
            throw new AuthException(ErrorCode.USER_DISABLED);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthException(ErrorCode.PASSWORD_ERROR);
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), 
                user.getRole() != null ? user.getRole().getRoleCode() : "");

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUser(convertToResponse(user));
        return response;
    }

    @Transactional
    public UserResponse register(UserCreateRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException(ErrorCode.USERNAME_EXISTS);
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setStudentId(request.getStudentId());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setStatus(1);
        user.setDeleted(0);

        if (request.getRoleId() != null) {
            Role role = roleRepository.findById(request.getRoleId())
                    .orElseThrow(() -> new BusinessException("角色不存在"));
            user.setRole(role);
        } else {
            Role memberRole = roleRepository.findByRoleCode(Constants.ROLE_MEMBER)
                    .orElseThrow(() -> new BusinessException("默认角色不存在"));
            user.setRole(memberRole);
        }

        user = userRepository.save(user);
        return convertToResponse(user);
    }

    public UserResponse getCurrentUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return convertToResponse(user);
    }

    @Transactional
    public UserResponse updateProfile(Long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (request.getRealName() != null) {
            user.setRealName(request.getRealName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }

        user = userRepository.save(user);
        return convertToResponse(user);
    }

    @Transactional
    public void changePassword(Long userId, PasswordChangeRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_ERROR);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public Page<UserResponse> getUserList(Pageable pageable) {
        return userRepository.findAllActive(pageable).map(this::convertToResponse);
    }

    @Transactional
    public UserResponse updateUserStatus(Long userId, Integer status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        user.setStatus(status != null && status == 1 ? 1 : 0);
        user = userRepository.save(user);
        return convertToResponse(user);
    }

    @Transactional
    public UserResponse updateUser(Long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (request.getRealName() != null) {
            user.setRealName(request.getRealName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        if (request.getRoleId() != null) {
            Role role = roleRepository.findById(request.getRoleId())
                    .orElseThrow(() -> new BusinessException("角色不存在"));
            user.setRole(role);
        }

        user = userRepository.save(user);
        return convertToResponse(user);
    }

    private UserResponse convertToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setRealName(user.getRealName());
        response.setStudentId(user.getStudentId());
        response.setPhone(user.getPhone());
        response.setEmail(user.getEmail());
        response.setAvatar(user.getAvatar());
        response.setStatus(user.getStatus());
        response.setCreateTime(user.getCreateTime());

        if (user.getRole() != null) {
            RoleResponse roleResponse = new RoleResponse();
            roleResponse.setId(user.getRole().getId());
            roleResponse.setRoleName(user.getRole().getRoleName());
            roleResponse.setRoleCode(user.getRole().getRoleCode());
            roleResponse.setDescription(user.getRole().getDescription());
            response.setRole(roleResponse);

            List<String> permissions = PermissionUtil.parsePermissions(user.getRole().getPermissions());
            response.setPermissions(permissions);
        }

        return response;
    }
}
