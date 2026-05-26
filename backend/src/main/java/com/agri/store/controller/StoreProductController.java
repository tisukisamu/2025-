package com.agri.store.controller;

import com.agri.store.dto.StoreProductRequest;
import com.agri.store.entity.Product;
import com.agri.store.entity.Store;
import com.agri.store.entity.StockLog;
import com.agri.store.entity.User;
import com.agri.store.repository.CategoryRepository;
import com.agri.store.repository.ProductRepository;
import com.agri.store.repository.StoreRepository;
import com.agri.store.repository.StockLogRepository;
import com.agri.store.repository.UserRepository;
import com.agri.store.security.JwtTokenProvider;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/store/product")
public class StoreProductController {

    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final StockLogRepository stockLogRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final JwtTokenProvider tokenProvider;

    public StoreProductController(ProductRepository productRepository, StoreRepository storeRepository,
                                  StockLogRepository stockLogRepository, UserRepository userRepository,
                                  CategoryRepository categoryRepository, JwtTokenProvider tokenProvider) {
        this.productRepository = productRepository;
        this.storeRepository = storeRepository;
        this.stockLogRepository = stockLogRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
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
    public ResponseEntity<?> getMyProducts(@RequestHeader("Authorization") String authHeader,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size,
                                           @RequestParam(required = false) String keyword,
                                           @RequestParam(required = false) Integer status,
                                           @RequestParam(required = false) Long categoryId,
                                           @RequestParam(required = false) Boolean stockWarning) {
        try {
            User user = getCurrentUser(authHeader);

            // 管理员可以查看所有商品
            if ("ROLE_ADMIN".equals(user.getRole())) {
                Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());
                Page<Product> products;
                
                // 构建查询条件
                if (keyword != null && !keyword.isEmpty() || status != null || categoryId != null || Boolean.TRUE.equals(stockWarning)) {
                    products = productRepository.findAll(pageable);
                    // 在内存中过滤（临时方案，可以后续添加复合查询）
                    java.util.List<Product> filteredProducts = products.getContent();
                    
                    if (keyword != null && !keyword.isEmpty()) {
                        final String kw = keyword.toLowerCase();
                        filteredProducts = filteredProducts.stream()
                                .filter(p -> p.getName() != null && p.getName().toLowerCase().contains(kw))
                                .toList();
                    }
                    if (status != null) {
                        final Integer st = status;
                        filteredProducts = filteredProducts.stream()
                                .filter(p -> st.equals(p.getStatus()))
                                .toList();
                    }
                    if (categoryId != null) {
                        final Long catId = categoryId;
                        filteredProducts = filteredProducts.stream()
                                .filter(p -> catId.equals(p.getCategoryId()))
                                .toList();
                    }
                    if (Boolean.TRUE.equals(stockWarning)) {
                        filteredProducts = filteredProducts.stream()
                                .filter(p -> p.getStock() != null && p.getStockWarning() != null
                                        && p.getStock() <= p.getStockWarning())
                                .toList();
                    }
                    
                    // 返回过滤后的结果
                    return ResponseEntity.ok(new org.springframework.data.domain.PageImpl<>(
                            filteredProducts,
                            pageable,
                            filteredProducts.size()
                    ));
                } else {
                    products = productRepository.findAll(pageable);
                }
                return ResponseEntity.ok(products);
            }

            // 普通店家只能查看自己的商品
            // 如果店铺不存在或正在审核中，返回空列表，让用户可以进入后台查看
            java.util.Optional<Store> storeOpt = storeRepository.findByUserId(user.getId());
            if (storeOpt.isEmpty()) {
                // 店铺不存在，返回空列表
                return ResponseEntity.ok(new org.springframework.data.domain.PageImpl<Product>(
                        java.util.Collections.emptyList(),
                        PageRequest.of(page, size, Sort.by("createTime").descending()),
                        0
                ));
            }
            
            Store store = storeOpt.get();
            Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());

            // 普通店家按分类筛选
            if (categoryId != null) {
                Page<Product> products = productRepository.findByStoreIdAndCategoryId(store.getId(), categoryId, pageable);
                return ResponseEntity.ok(products);
            }
            
            // 其他筛选条件
            Page<Product> products;
            if (keyword != null && !keyword.isEmpty()) {
                products = productRepository.findByStoreIdAndNameContainingIgnoreCase(store.getId(), keyword, pageable);
            } else {
                products = productRepository.findByStoreId(store.getId(), pageable);
            }
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("加载商品失败: " + e.getMessage());
        }
    }

    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createProduct(@Valid @RequestBody StoreProductRequest request,
                                           @RequestHeader("Authorization") String authHeader) {
        User user = getCurrentUser(authHeader);
        Store store = getMyStore(user.getId(), user);

        // 检查店铺审核状态
        if (store.getStatus() == null || store.getStatus() != 1) {
            String message;
            switch (store.getStatus() != null ? store.getStatus() : -1) {
                case 0:
                    message = "您的店铺正在审核中，请耐心等待审核通过后再发布商品";
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

        Product product = new Product();
        product.setStoreId(store.getId());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setStockWarning(request.getStockWarning());
        
        // 存储主图URL（相对路径）到 imageUrl
        if (request.getMainImageUrl() != null) {
            String mainImageUrl = request.getMainImageUrl();
            // 提取相对路径（如果是绝对路径，提取路径部分）
            if (mainImageUrl.startsWith("http://") || mainImageUrl.startsWith("https://")) {
                try {
                    java.net.URL urlObj = new java.net.URL(mainImageUrl);
                    mainImageUrl = urlObj.getPath();
                } catch (Exception e) {
                    // 保持原样
                }
            }
            // 确保以 / 开头
            if (!mainImageUrl.startsWith("/")) {
                mainImageUrl = "/" + mainImageUrl;
            }
            product.setImageUrl(mainImageUrl);
        }
        
        // 存储图片URL列表（相对路径）到 imageUrls，最多4张
        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            // 限制最多4张
            List<String> limitedUrls = request.getImageUrls().stream()
                .limit(4)
                .map(url -> {
                    if (url == null) return "";
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
                })
                .filter(url -> !url.isEmpty())
                .toList();
            product.setImageUrls(String.join(",", limitedUrls));
        }
        
        product.setCategoryId(request.getCategoryId());

        categoryRepository.findById(request.getCategoryId())
                .ifPresent(cat -> product.setCategory(cat.getName()));

        product.setStatus(0); // 待审核
        product.setActive(false);
        product.setIsNew(false);
        product.setIsHot(false);
        product.setSales(0);

        Product saved = productRepository.save(product);

        // 记录库存日志
        StockLog log = new StockLog();
        log.setProductId(saved.getId());
        log.setStoreId(store.getId());
        log.setChangeAmount(request.getStock());
        log.setBeforeStock(0);
        log.setAfterStock(request.getStock());
        log.setType("RESTOCK");
        log.setRemark("新品上架");
        log.setOperatorId(user.getId());
        log.setOperatorName(user.getUsername()); // Assuming username or nickname
        stockLogRepository.save(log);

        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateProduct(@PathVariable Long id,
                                           @Valid @RequestBody StoreProductRequest request,
                                           @RequestHeader("Authorization") String authHeader) {
        User user = getCurrentUser(authHeader);
        boolean isAdmin = "ROLE_ADMIN".equals(user.getRole());
        Store store = getMyStore(user.getId(), user);

        return productRepository.findById(id)
                .map(product -> {
                    // 检查权限：管理员可以修改所有商品，普通店家只能修改自己的商品
                    Long productStoreId = product.getStoreId();
                    if (!isAdmin) {
                        // 普通店家：商品必须属于自己的店铺
                        if (productStoreId == null || !productStoreId.equals(store.getId())) {
                            return ResponseEntity.status(403).body("无权修改此商品");
                        }
                    }

                    product.setName(request.getName());
                    product.setDescription(request.getDescription());
                    product.setPrice(request.getPrice());

                    // 库存变动记录
                    if (!product.getStock().equals(request.getStock())) {
                        int diff = request.getStock() - product.getStock();
                        StockLog log = new StockLog();
                        log.setProductId(product.getId());
                        log.setStoreId(store.getId());
                        log.setChangeAmount(diff);
                        log.setBeforeStock(product.getStock());
                        log.setAfterStock(request.getStock());
                        log.setType("ADJUST");
                        log.setRemark("店家修改库存");
                        log.setOperatorId(user.getId());
                        log.setOperatorName(user.getUsername());
                        stockLogRepository.save(log);
                    }

                    product.setStock(request.getStock());
                    product.setStockWarning(request.getStockWarning());
                    
                    // 存储主图URL（相对路径）到 imageUrl
                    if (request.getMainImageUrl() != null) {
                        String mainImageUrl = request.getMainImageUrl();
                        // 提取相对路径（如果是绝对路径，提取路径部分）
                        if (mainImageUrl.startsWith("http://") || mainImageUrl.startsWith("https://")) {
                            try {
                                java.net.URL urlObj = new java.net.URL(mainImageUrl);
                                mainImageUrl = urlObj.getPath();
                            } catch (Exception e) {
                                // 保持原样
                            }
                        }
                        // 确保以 / 开头
                        if (!mainImageUrl.startsWith("/")) {
                            mainImageUrl = "/" + mainImageUrl;
                        }
                        product.setImageUrl(mainImageUrl);
                    }
                    
                    // 存储图片URL列表（相对路径）到 imageUrls，最多4张
                    if (request.getImageUrls() != null) {
                        // 限制最多4张
                        List<String> limitedUrls = request.getImageUrls().stream()
                            .limit(4)
                            .map(url -> {
                                if (url == null) return "";
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
                            })
                            .filter(url -> !url.isEmpty())
                            .toList();
                        product.setImageUrls(String.join(",", limitedUrls));
                    }

                    // 检查分类是否变更（处理categoryId为null的情况）
                    Long currentCategoryId = product.getCategoryId();
                    Long newCategoryId = request.getCategoryId();
                    if (currentCategoryId == null ? newCategoryId != null : !currentCategoryId.equals(newCategoryId)) {
                         product.setCategoryId(newCategoryId);
                         if (newCategoryId != null) {
                             categoryRepository.findById(newCategoryId)
                                    .ifPresent(cat -> product.setCategory(cat.getName()));
                         }
                    }

                    return ResponseEntity.ok(productRepository.save(product));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id,
                                           @RequestHeader("Authorization") String authHeader) {
        User user = getCurrentUser(authHeader);
        boolean isAdmin = "ROLE_ADMIN".equals(user.getRole());
        Store store = getMyStore(user.getId(), user);

        return productRepository.findById(id)
                .map(product -> {
                    // 检查权限：管理员可以删除所有商品，普通店家只能删除自己的商品
                    Long productStoreId = product.getStoreId();
                    if (!isAdmin) {
                        // 普通店家：商品必须属于自己的店铺
                        if (productStoreId == null || !productStoreId.equals(store.getId())) {
                            return ResponseEntity.status(403).body("无权删除此商品");
                        }
                    }
                    productRepository.delete(product);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/active")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> toggleProduct(@PathVariable Long id,
                                           @RequestBody Map<String, Boolean> body,
                                           @RequestHeader("Authorization") String authHeader) {
        User user = getCurrentUser(authHeader);
        boolean isAdmin = "ROLE_ADMIN".equals(user.getRole());
        Store store = getMyStore(user.getId(), user);
        Boolean active = body.get("active");

        return productRepository.findById(id)
                .map(product -> {
                    // 检查权限：管理员可以操作所有商品，普通店家只能操作自己的商品
                    Long productStoreId = product.getStoreId();
                    if (!isAdmin) {
                        // 普通店家：商品必须属于自己的店铺
                        if (productStoreId == null || !productStoreId.equals(store.getId())) {
                            return ResponseEntity.status(403).body("无权操作此商品");
                        }
                    }
                    if (product.getStatus() != 1) {
                        return ResponseEntity.badRequest().body("商品未通过审核，无法上下架");
                    }
                    product.setActive(active);
                    return ResponseEntity.ok(productRepository.save(product));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    @PostMapping("/upload")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> uploadImage(@RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("文件为空");
            }
            String fileName = java.util.UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            java.nio.file.Path path = java.nio.file.Paths.get("uploads/" + fileName);
            java.nio.file.Files.createDirectories(path.getParent());
            java.nio.file.Files.write(path, file.getBytes());

            return ResponseEntity.ok("/api/uploads/" + fileName);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("上传失败: " + e.getMessage());
        }
    }

    @GetMapping("/warning")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getStockWarning(@RequestHeader("Authorization") String authHeader) {
        try {
            User user = getCurrentUser(authHeader);

            // 管理员查看所有库存预警商品
            if ("ROLE_ADMIN".equals(user.getRole())) {
                List<Product> warningProducts = productRepository.findAll().stream()
                        .filter(p -> p.getStock() != null && p.getStock() <= p.getStockWarning())
                        .toList();
                return ResponseEntity.ok(warningProducts);
            }

            // 普通店家查看自己店铺的库存预警
            // 如果店铺不存在，返回空列表
            java.util.Optional<Store> storeOpt = storeRepository.findByUserId(user.getId());
            if (storeOpt.isEmpty()) {
                return ResponseEntity.ok(java.util.Collections.emptyList());
            }
            
            Store store = storeOpt.get();
            Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 1000);
            List<Product> storeProducts = productRepository.findByStoreId(store.getId(), pageable).getContent();
            List<Product> warningProducts = storeProducts.stream()
                    .filter(p -> p.getStock() != null && p.getStock() <= p.getStockWarning())
                    .toList();
            return ResponseEntity.ok(warningProducts);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("获取库存预警失败: " + e.getMessage());
        }
    }
}
