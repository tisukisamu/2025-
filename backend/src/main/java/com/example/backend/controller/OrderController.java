package com.example.backend.controller;

import com.example.backend.dto.request.OrderRequest;
import com.example.backend.dto.response.ApiResponse;
import com.example.backend.dto.response.OrderDTO;
import com.example.backend.dto.response.PageResponse;
import com.example.backend.security.UserDetailsImpl;
import com.example.backend.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<OrderDTO>>> getOrders(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<OrderDTO> orders = orderService.getOrders(userDetails.getId(), page, size);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDTO>> getOrderDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        OrderDTO order = orderService.getOrderDetail(id, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(order));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrderDTO>> createOrder(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody OrderRequest request) {
        OrderDTO order = orderService.createOrder(userDetails.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("订单创建成功", order));
    }

    @PutMapping("/{id}/ship")
    public ResponseEntity<ApiResponse<OrderDTO>> shipOrder(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam String expressNo) {
        OrderDTO order = orderService.shipOrder(id, userDetails.getId(), expressNo);
        return ResponseEntity.ok(ApiResponse.success("发货成功", order));
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<ApiResponse<OrderDTO>> confirmOrder(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        OrderDTO order = orderService.confirmOrder(id, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("确认收货成功", order));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderDTO>> cancelOrder(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(required = false) String reason) {
        OrderDTO order = orderService.cancelOrder(id, userDetails.getId(), reason);
        return ResponseEntity.ok(ApiResponse.success("订单已取消", order));
    }
}
