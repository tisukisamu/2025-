package com.agri.store.controller;

import com.agri.store.dto.StoreRegisterRequest;
import com.agri.store.entity.Store;
import com.agri.store.repository.StoreRepository;
import com.agri.store.repository.UserRepository;
import com.agri.store.security.JwtTokenProvider;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/store")
public class StoreController {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;

    public StoreController(StoreRepository storeRepository, UserRepository userRepository, JwtTokenProvider tokenProvider) {
        this.storeRepository = storeRepository;
        this.userRepository = userRepository;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/register")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> registerStore(@Valid @RequestBody StoreRegisterRequest request,
                                           @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String username = tokenProvider.getUsernameFromJWT(token);
        
        Long userId = userRepository.findByUsername(username)
                .map(user -> user.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (storeRepository.existsByUserId(userId)) {
            return ResponseEntity.badRequest().body("您已经注册过店铺了");
        }

        if (storeRepository.existsByStoreName(request.getStoreName())) {
            return ResponseEntity.badRequest().body("店铺名称已被使用");
        }

        Store store = new Store();
        store.setUserId(userId);
        store.setStoreName(request.getStoreName());
        store.setDescription(request.getDescription());
        store.setPhone(request.getPhone());
        store.setAddress(request.getAddress());
        store.setLogoUrl(request.getLogoUrl());
        store.setStatus(0); // 待审核

        Store savedStore = storeRepository.save(store);
        return ResponseEntity.ok(savedStore);
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getMyStore(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String username = tokenProvider.getUsernameFromJWT(token);

        Long userId = userRepository.findByUsername(username)
                .map(user -> user.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 检查是否是管理员，如果是则自动创建默认店铺
        var user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

        if ("ROLE_ADMIN".equals(user.getRole())) {
            return ResponseEntity.ok(storeRepository.findByUserId(userId)
                    .orElseGet(() -> {
                        Store defaultStore = new Store();
                        defaultStore.setUserId(userId);
                        defaultStore.setStoreName("平台自营店");
                        defaultStore.setDescription("平台自营商品");
                        defaultStore.setStatus(1); // 直接通过审核
                        defaultStore.setAuditTime(java.time.LocalDateTime.now());
                        return storeRepository.save(defaultStore);
                    }));
        }

        return storeRepository.findByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateMyStore(@Valid @RequestBody StoreRegisterRequest request,
                                           @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String username = tokenProvider.getUsernameFromJWT(token);
        
        Long userId = userRepository.findByUsername(username)
                .map(user -> user.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return storeRepository.findByUserId(userId)
                .map(store -> {
                    store.setStoreName(request.getStoreName());
                    store.setDescription(request.getDescription());
                    store.setPhone(request.getPhone());
                    store.setAddress(request.getAddress());
                    if (request.getLogoUrl() != null) {
                        store.setLogoUrl(request.getLogoUrl());
                    }
                    return ResponseEntity.ok(storeRepository.save(store));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Store>> getPendingStores() {
        return ResponseEntity.ok(storeRepository.findByStatus(0));
    }

    @PostMapping("/audit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> auditStore(@PathVariable Long id, @RequestBody com.agri.store.dto.AuditRequest request,
                                        @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String username = tokenProvider.getUsernameFromJWT(token);
        
        Long adminId = userRepository.findByUsername(username)
                .map(user -> user.getId())
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        return storeRepository.findById(id)
                .map(store -> {
                    store.setStatus(request.getStatus());
                    store.setRejectReason(request.getReason());
                    store.setAuditBy(adminId);
                    store.setAuditTime(java.time.LocalDateTime.now());
                    return ResponseEntity.ok(storeRepository.save(store));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
