package com.agri.store.controller;

import com.agri.store.dto.OrderDTO;
import com.agri.store.entity.Order;
import com.agri.store.entity.Store;
import com.agri.store.entity.User;
import com.agri.store.repository.OrderRepository;
import com.agri.store.repository.StoreRepository;
import com.agri.store.repository.UserRepository;
import com.agri.store.service.OrderService;
import com.agri.store.security.JwtTokenProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/store/order")
public class StoreOrderController {

    private final OrderRepository orderRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final OrderService orderService;
    private final JwtTokenProvider tokenProvider;

    public StoreOrderController(OrderRepository orderRepository, StoreRepository storeRepository,
                                UserRepository userRepository, OrderService orderService,
                                JwtTokenProvider tokenProvider) {
        this.orderRepository = orderRepository;
        this.storeRepository = storeRepository;
        this.userRepository = userRepository;
        this.orderService = orderService;
        this.tokenProvider = tokenProvider;
    }

    private User getCurrentUser(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String username = tokenProvider.getUsernameFromJWT(token);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private Store getMyStore(Long userId, User user) {
        // 为管理员自动创建默认店铺
        if ("ROLE_ADMIN".equals(user.getRole())) {
            return storeRepository.findByUserId(userId)
                    .orElseGet(() -> {
                        Store defaultStore = new Store();
                        defaultStore.setUserId(userId);
                        defaultStore.setStoreName("平台自营店");
                        defaultStore.setDescription("平台自营商品");
                        defaultStore.setStatus(1); // 直接通过审核
                        defaultStore.setAuditTime(java.time.LocalDateTime.now());
                        return storeRepository.save(defaultStore);
                    });
        }
        return storeRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("您还没有注册店铺"));
    }

    @GetMapping("/list")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<OrderDTO>> getStoreOrders(@RequestHeader("Authorization") String authHeader) {
        User user = getCurrentUser(authHeader);

        // 管理员查看所有订单
        if ("ROLE_ADMIN".equals(user.getRole())) {
            List<Order> orders = orderRepository.findAll();
            List<OrderDTO> dtos = orders.stream()
                    .map(OrderDTO::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        }

        // 普通店家查看自己店铺的订单
        // 如果店铺不存在，返回空列表
        java.util.Optional<Store> storeOpt = storeRepository.findByUserId(user.getId());
        if (storeOpt.isEmpty()) {
            return ResponseEntity.ok(java.util.Collections.emptyList());
        }
        
        Store store = storeOpt.get();

        List<Order> orders = orderRepository.findByStoreId(store.getId());
        List<OrderDTO> dtos = orders.stream()
                .map(OrderDTO::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/{id}/ship")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> shipOrder(@PathVariable Long id,
                                       @RequestBody Map<String, String> body,
                                       @RequestHeader("Authorization") String authHeader) {
        User user = getCurrentUser(authHeader);

        // 管理员可以直接发货
        if (!"ROLE_ADMIN".equals(user.getRole())) {
            // 检查店铺状态
            java.util.Optional<Store> storeOpt = storeRepository.findByUserId(user.getId());
            if (storeOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("您还没有注册店铺");
            }
            
            Store store = storeOpt.get();
            
            // 检查店铺审核状态
            if (store.getStatus() == null || store.getStatus() != 1) {
                String message;
                switch (store.getStatus() != null ? store.getStatus() : -1) {
                    case 0:
                        message = "您的店铺正在审核中，请耐心等待审核通过后再操作";
                        break;
                    case 2:
                        message = "您的店铺审核未通过，请根据审核意见修改后重新提交";
                        break;
                    case 3:
                        message = "您的店铺已被禁用，如有疑问请联系平台客服";
                        break;
                    default:
                        message = "您尚未完成店铺注册，请先注册店铺";
                        break;
                }
                return ResponseEntity.badRequest().body(message);
            }
            
            String trackingNo = body.get("trackingNo");

            Order order = orderRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            // Verify that the order contains items from this store (handle null storeId)
            boolean belongsToStore = order.getItems().stream()
                    .anyMatch(item -> item.getStoreId() != null && item.getStoreId().equals(store.getId()));

            if (!belongsToStore) {
                return ResponseEntity.status(403).body("无权操作此订单");
            }
        }

        String trackingNo = body.get("trackingNo");
        orderService.shipOrder(id, trackingNo);
        return ResponseEntity.ok().build();
    }
}
