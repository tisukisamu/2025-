package com.example.backend.controller;

import com.example.backend.dto.response.ApiResponse;
import com.example.backend.dto.response.OrderDTO;
import com.example.backend.dto.response.PageResponse;
import com.example.backend.dto.response.ProductDTO;
import com.example.backend.dto.response.UserDTO;
import com.example.backend.entity.Order;
import com.example.backend.entity.Product;
import com.example.backend.entity.User;
import com.example.backend.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "*")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardStats() {
        Map<String, Object> stats = adminService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<PageResponse<UserDTO>>> getUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<UserDTO> users = adminService.getUsers(page, size);
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @PutMapping("/users/{id}/status")
    public ResponseEntity<ApiResponse<UserDTO>> updateUserStatus(
            @PathVariable Long id,
            @RequestParam User.Status status) {
        UserDTO user = adminService.updateUserStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("状态更新成功", user));
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<ApiResponse<UserDTO>> updateUserRole(
            @PathVariable Long id,
            @RequestParam User.Role role) {
        UserDTO user = adminService.updateUserRole(id, role);
        return ResponseEntity.ok(ApiResponse.success("角色更新成功", user));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("用户删除成功", null));
    }

    @GetMapping("/products")
    public ResponseEntity<ApiResponse<PageResponse<ProductDTO>>> getProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Product.AuditStatus auditStatus) {
        PageResponse<ProductDTO> products = adminService.getProducts(page, size, auditStatus);
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @PutMapping("/products/{id}/audit")
    public ResponseEntity<ApiResponse<ProductDTO>> auditProduct(
            @PathVariable Long id,
            @RequestParam Product.AuditStatus auditStatus,
            @RequestParam(required = false) String reason) {
        ProductDTO product = adminService.auditProduct(id, auditStatus, reason);
        return ResponseEntity.ok(ApiResponse.success("审核完成", product));
    }

    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<PageResponse<OrderDTO>>> getOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Order.OrderStatus status) {
        PageResponse<OrderDTO> orders = adminService.getOrders(page, size, status);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }
}
