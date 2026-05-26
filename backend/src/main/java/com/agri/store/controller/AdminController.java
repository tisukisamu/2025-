package com.agri.store.controller;

import com.agri.store.entity.OperationLog;
import com.agri.store.entity.Order;
import com.agri.store.entity.Product;
import com.agri.store.entity.Store;
import com.agri.store.entity.User;
import com.agri.store.repository.OperationLogRepository;
import com.agri.store.repository.OrderRepository;
import com.agri.store.repository.ProductRepository;
import com.agri.store.repository.StoreRepository;
import com.agri.store.repository.UserRepository;
import com.agri.store.service.OrderService;
import com.agri.store.security.JwtTokenProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 重新导入确保没有缓存问题

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final OrderService orderService;
    private final OperationLogRepository operationLogRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AdminController(OrderRepository orderRepository, UserRepository userRepository,
                          ProductRepository productRepository, StoreRepository storeRepository,
                          OrderService orderService, OperationLogRepository operationLogRepository,
                          PasswordEncoder passwordEncoder, JwtTokenProvider tokenProvider) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.storeRepository = storeRepository;
        this.orderService = orderService;
        this.operationLogRepository = operationLogRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    private User getCurrentUser(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authHeader.replace("Bearer ", "");
        String username = tokenProvider.getUsernameFromJWT(token);
        return userRepository.findByUsername(username).orElse(null);
    }

    // 店铺管理接口
    @GetMapping("/stores")
    public Page<Store> getStores(@RequestParam(required = false) Integer status,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());
        if (status != null) {
            return storeRepository.findByStatus(status, pageable);
        }
        return storeRepository.findAll(pageable);
    }

    @PutMapping("/store/{id}/audit")
    public ResponseEntity<?> auditStore(@PathVariable Long id,
                                      @RequestBody Map<String, Object> body,
                                      @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Boolean pass = (Boolean) body.get("pass");
        String reason = (String) body.get("reason");
        User admin = getCurrentUser(authHeader);

        return storeRepository.findById(id)
                .map(store -> {
                    if (Boolean.TRUE.equals(pass)) {
                        store.setStatus(1); // 通过
                        // 升级用户角色为 ROLE_STORE
                        userRepository.findById(store.getUserId()).ifPresent(user -> {
                            if (!"ROLE_ADMIN".equals(user.getRole())) {
                                user.setRole("ROLE_STORE");
                                userRepository.save(user);
                            }
                        });
                    } else {
                        store.setStatus(2); // 驳回
                        store.setRejectReason(reason);
                    }
                    store.setAuditTime(LocalDateTime.now());
                    if (admin != null) {
                        store.setAuditBy(admin.getId());
                    }
                    return ResponseEntity.ok(storeRepository.save(store));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // 商品管理接口
    @GetMapping("/products")
    public Page<Product> getProducts(@RequestParam(required = false) Integer status,
                                     @RequestParam(required = false) String keyword,
                                     @RequestParam(required = false) Long categoryId,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());

        // 按状态筛选
        if (status != null) {
            if (keyword != null && !keyword.trim().isEmpty()) {
                return productRepository.findByStatusAndNameContainingIgnoreCase(status, keyword.trim(), pageable);
            } else if (categoryId != null) {
                return productRepository.findByStatusAndCategoryId(status, categoryId, pageable);
            }
            return productRepository.findByStatus(status, pageable);
        }

        // 按关键词搜索
        if (keyword != null && !keyword.trim().isEmpty()) {
            return productRepository.findByNameContainingIgnoreCase(keyword.trim(), pageable);
        }

        // 按分类筛选
        if (categoryId != null) {
            return productRepository.findByCategoryId(categoryId, pageable);
        }

        return productRepository.findAll(pageable);
    }

    @PostMapping("/product")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        product.setCreateTime(LocalDateTime.now());
        product.setStatus(0); // 待审核
        product.setActive(false); // 审核通过前不显示
        
        // 处理图片URL：确保存储相对路径
        if (product.getImageUrl() != null) {
            product.setImageUrl(extractRelativePath(product.getImageUrl()));
        }
        if (product.getImageUrls() != null) {
            product.setImageUrls(extractRelativePaths(product.getImageUrls()));
        }
        
        return ResponseEntity.ok(productRepository.save(product));
    }

    @PutMapping("/product/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
        return productRepository.findById(id)
                .map(product -> {
                    product.setName(productDetails.getName());
                    product.setDescription(productDetails.getDescription());
                    product.setPrice(productDetails.getPrice());
                    product.setStock(productDetails.getStock());
                    
                    // 处理图片URL：确保存储相对路径
                    if (productDetails.getImageUrl() != null) {
                        product.setImageUrl(extractRelativePath(productDetails.getImageUrl()));
                    }
                    if (productDetails.getImageUrls() != null) {
                        product.setImageUrls(extractRelativePaths(productDetails.getImageUrls()));
                    }
                    
                    product.setCategoryId(productDetails.getCategoryId());
                    product.setCategory(productDetails.getCategory());
                    product.setIsNew(productDetails.getIsNew());
                    product.setIsHot(productDetails.getIsHot());
                    product.setUpdateTime(LocalDateTime.now());
                    return ResponseEntity.ok(productRepository.save(product));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    // 辅助方法：提取相对路径
    private String extractRelativePath(String url) {
        if (url == null) return null;
        String processedUrl = url;
        // 如果是绝对路径，提取路径部分
        if (url.startsWith("http://") || url.startsWith("https://")) {
            try {
                java.net.URL urlObj = new java.net.URL(url);
                processedUrl = urlObj.getPath();
            } catch (Exception e) {
                processedUrl = url;
            }
        }
        // 确保以 / 开头
        if (!processedUrl.startsWith("/")) {
            processedUrl = "/" + processedUrl;
        }
        return processedUrl;
    }
    
    // 辅助方法：提取多个相对路径（逗号分隔）
    private String extractRelativePaths(String urls) {
        if (urls == null) return null;
        String[] urlArray = urls.split(",");
        List<String> processedUrls = new java.util.ArrayList<>();
        for (String url : urlArray) {
            String processed = extractRelativePath(url.trim());
            if (processed != null && !processed.isEmpty()) {
                processedUrls.add(processed);
            }
        }
        return String.join(",", processedUrls);
    }

    @DeleteMapping("/product/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        return productRepository.findById(id)
                .map(product -> {
                    // 删除商品关联的图片文件
                    deleteProductImages(product);

                    productRepository.delete(product);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 删除商品关联的图片文件
     */
    private void deleteProductImages(Product product) {
        try {
            // 获取所有图片URL
            List<String> imageUrls = new ArrayList<>();

            // 从 imageUrls 字段获取
            if (product.getImageUrls() != null && !product.getImageUrls().isEmpty()) {
                String[] urls = product.getImageUrls().split(",");
                for (String url : urls) {
                    if (url != null && !url.trim().isEmpty()) {
                        imageUrls.add(url.trim());
                    }
                }
            }

            // 从 imageUrl 字段获取（主图）
            if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                imageUrls.add(product.getImageUrl());
            }

            // 删除每个图片文件
            for (String imageUrl : imageUrls) {
                deleteImageFile(imageUrl);
            }
        } catch (Exception e) {
            // 记录错误但不影响删除商品的主流程
            System.err.println("删除商品图片失败: " + e.getMessage());
        }
    }

    /**
     * 删除单个图片文件
     */
    private void deleteImageFile(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return;
        }

        try {
            // 从URL中提取文件名
            // URL格式: /api/uploads/filename.jpg 或 /uploads/filename.jpg
            String fileName = imageUrl;
            if (imageUrl.contains("/")) {
                fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            }

            if (fileName.isEmpty()) {
                return;
            }

            // 构建文件路径
            File imageFile = new File("uploads/" + fileName);

            // 删除文件
            if (imageFile.exists() && imageFile.isFile()) {
                boolean deleted = imageFile.delete();
                if (deleted) {
                    System.out.println("已删除图片: " + imageFile.getAbsolutePath());
                } else {
                    System.err.println("删除图片失败: " + imageFile.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            System.err.println("删除图片文件失败: " + imageUrl + ", 错误: " + e.getMessage());
        }
    }

    @PatchMapping("/product/{id}/active")
    public ResponseEntity<Product> toggleProductActive(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Boolean active = (Boolean) body.get("active");
        return productRepository.findById(id)
                .map(product -> {
                    product.setActive(active != null ? active : false);
                    product.setUpdateTime(LocalDateTime.now());
                    return ResponseEntity.ok(productRepository.save(product));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/product/{id}/audit")
    public ResponseEntity<?> auditProduct(@PathVariable Long id,
                                        @RequestBody Map<String, Object> body,
                                        @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Boolean pass = (Boolean) body.get("pass");
        String reason = (String) body.get("reason");
        User admin = getCurrentUser(authHeader);

        return productRepository.findById(id)
                .map(product -> {
                    if (Boolean.TRUE.equals(pass)) {
                        product.setStatus(1); // 通过
                        product.setActive(true); // 审核通过后默认上架
                    } else {
                        product.setStatus(2); // 驳回
                        product.setActive(false);
                        product.setRejectReason(reason);
                    }
                    product.setAuditTime(LocalDateTime.now());
                    if (admin != null) {
                        product.setAuditBy(admin.getId());
                    }

                    return ResponseEntity.ok(productRepository.save(product));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // 订单管理接口
    @GetMapping("/orders")
    public Page<Order> getOrders(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());
        return orderRepository.findAll(pageable);
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<Order> getOrderDetail(@PathVariable Long id) {
        return orderRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/orders/{id}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Integer status = (Integer) body.get("status");
        return orderRepository.findById(id)
                .map(order -> {
                    order.setStatus(status);
                    return ResponseEntity.ok(orderRepository.save(order));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // 用户管理接口
    @GetMapping("/users")
    public Page<User> getUsers(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());
        return userRepository.findAll(pageable);
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest().build();
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreateTime(LocalDateTime.now());
        return ResponseEntity.ok(userRepository.save(user));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setNickname(userDetails.getNickname());
                    user.setEmail(userDetails.getEmail());
                    user.setPhone(userDetails.getPhone());
                    user.setRole(userDetails.getRole());
                    user.setUpdateTime(LocalDateTime.now());
                    return ResponseEntity.ok(userRepository.save(user));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    userRepository.delete(user);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/users/{id}/status")
    public ResponseEntity<User> updateUserStatus(@PathVariable Long id, @RequestParam boolean active) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setActive(active);
                    user.setUpdateTime(LocalDateTime.now());
                    return ResponseEntity.ok(userRepository.save(user));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/users/{id}/reset-password")
    public ResponseEntity<?> resetUserPassword(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    String newPassword = "123456"; // 默认重置为 123456
                    user.setPassword(passwordEncoder.encode(newPassword));
                    user.setUpdateTime(LocalDateTime.now());
                    userRepository.save(user);
                    Map<String, String> response = new HashMap<>();
                    response.put("message", "密码重置成功");
                    response.put("newPassword", newPassword);
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/logs")
    public List<OperationLog> getOperationLogs() {
        return operationLogRepository.findAllByOrderByCreateTimeDesc();
    }

    @GetMapping("/reports/sales")
    public Map<String, Object> getSalesReport(@RequestParam String type,
                                             @RequestParam String startDate,
                                             @RequestParam String endDate) {
        // 简单实现，实际项目中可能需要更复杂的聚合查询
        Map<String, Object> report = new HashMap<>();
        List<Order> orders = orderRepository.findAll(); // 这里可以根据日期筛选优化

        // 模拟返回数据结构
        report.put("labels", List.of("周一", "周二", "周三", "周四", "周五", "周六", "周日"));
        report.put("sales", List.of(1200, 1900, 3000, 5000, 2300, 3400, 4500));
        report.put("orders", List.of(12, 19, 30, 50, 23, 34, 45));

        return report;
    }

    @GetMapping("/reports/categories")
    public List<Map<String, Object>> getCategoryStats() {
        // 模拟分类统计数据
        return List.of(
            Map.of("category", "新鲜水果", "productCount", 120, "salesCount", 500),
            Map.of("category", "时令蔬菜", "productCount", 85, "salesCount", 320),
            Map.of("category", "肉禽蛋品", "productCount", 45, "salesCount", 150),
            Map.of("category", "海鲜水产", "productCount", 30, "salesCount", 90)
        );
    }

    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();

        // 获取所有数据
        List<User> users = userRepository.findAll();
        List<Order> orders = orderRepository.findAll();
        List<Product> products = productRepository.findAll();

        // 计算今日销售额
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().atTime(LocalTime.MAX);

        BigDecimal todaySales = orders.stream()
                .filter(o -> o.getCreateTime().isAfter(todayStart) && o.getCreateTime().isBefore(todayEnd))
                .filter(o -> o.getStatus() != 0 && o.getStatus() != 4) // 已支付
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        stats.put("totalUsers", users.size());
        stats.put("totalOrders", orders.size());
        stats.put("totalProducts", products.size());
        stats.put("todaySales", todaySales);

        return stats;
    }
}
