package com.agri.store.controller;

import com.agri.store.dto.OrderDTO;
import com.agri.store.entity.Order;
import com.agri.store.entity.User;
import com.agri.store.repository.OrderRepository;
import com.agri.store.repository.UserRepository;
import com.agri.store.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final UserRepository userRepository;

    public OrderController(OrderRepository orderRepository, OrderService orderService, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.orderService = orderService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<OrderDTO> getMyOrders(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Order> orders = orderRepository.findByUserIdOrderByCreateTimeDesc(user.getId());
        return orders.stream()
                .map(OrderDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @PostMapping
    public OrderDTO createOrder(@Valid @RequestBody Order order, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Order createdOrder = orderService.createOrder(user, order);
        return OrderDTO.fromEntity(createdOrder);
    }

    @PostMapping("/{id}/pay")
    public OrderDTO payOrder(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Order order = orderService.payOrder(id, user);
        return OrderDTO.fromEntity(order);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return orderRepository.findById(id)
                .filter(order -> order.getUserId() != null && order.getUserId().equals(user.getId()))
                .map(OrderDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/tracking/{orderNo}")
    public ResponseEntity<OrderDTO> trackOrder(@PathVariable String orderNo) {
        return orderRepository.findByOrderNo(orderNo)
                .map(OrderDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
