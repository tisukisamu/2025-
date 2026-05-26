package com.example.backend.controller;

import com.example.backend.dto.FavoriteDTO;
import com.example.backend.dto.request.ChangePasswordRequest;
import com.example.backend.dto.request.UpdateUserRequest;
import com.example.backend.dto.response.ApiResponse;
import com.example.backend.dto.response.OrderDTO;
import com.example.backend.dto.response.PageResponse;
import com.example.backend.dto.response.UserDTO;
import com.example.backend.security.UserDetailsImpl;
import com.example.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDTO>> getCurrentUser(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        UserDTO user = userService.getUserInfo(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserDTO>> updateCurrentUser(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody UpdateUserRequest request) {
        UserDTO user = userService.updateUserInfo(userDetails.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("信息更新成功", user));
    }

    @PutMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(userDetails.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("密码修改成功", null));
    }

    @GetMapping("/me/orders")
    public ResponseEntity<ApiResponse<PageResponse<OrderDTO>>> getMyOrders(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<OrderDTO> orders = userService.getUserOrders(userDetails.getId(), page, size);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @GetMapping("/me/favorites")
    public ResponseEntity<ApiResponse<PageResponse<FavoriteDTO>>> getMyFavorites(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<FavoriteDTO> favorites = userService.getUserFavorites(userDetails.getId(), page, size);
        return ResponseEntity.ok(ApiResponse.success(favorites));
    }

    @PostMapping("/me/favorites/{productId}")
    public ResponseEntity<ApiResponse<Void>> addFavorite(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long productId) {
        userService.addFavorite(userDetails.getId(), productId);
        return ResponseEntity.ok(ApiResponse.success("收藏成功", null));
    }

    @DeleteMapping("/me/favorites/{productId}")
    public ResponseEntity<ApiResponse<Void>> removeFavorite(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long productId) {
        userService.removeFavorite(userDetails.getId(), productId);
        return ResponseEntity.ok(ApiResponse.success("取消收藏成功", null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable Long id) {
        UserDTO user = userService.getPublicUserInfo(id);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
}
