package com.agri.store.controller;

import com.agri.store.entity.Product;
import com.agri.store.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @GetMapping("/search")
    public Page<Product> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByActiveTrueAndNameContainingIgnoreCase(keyword, pageable);
    }

    @GetMapping("/new")
    public List<Product> getNewProducts() {
        return productRepository.findByActiveTrueAndIsNewTrueOrderBySalesDesc();
    }

    @GetMapping("/hot")
    public List<Product> getHotProducts() {
        return productRepository.findByActiveTrueAndIsHotTrueOrderBySalesDesc();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productRepository.findByIdAndActiveTrue(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.<Void>notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Product createProduct(@RequestBody Product product) {
        return productRepository.save(product);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
        return productRepository.findById(id)
                .map(product -> {
                    product.setName(productDetails.getName());
                    product.setDescription(productDetails.getDescription());
                    product.setPrice(productDetails.getPrice());
                    product.setStock(productDetails.getStock());
                    product.setImageUrl(productDetails.getImageUrl());
                    product.setActive(productDetails.getActive());
                    product.setCategory(productDetails.getCategory());
                    product.setIsNew(productDetails.getIsNew());
                    product.setIsHot(productDetails.getIsHot());
                    product.setSales(productDetails.getSales());
                    return ResponseEntity.ok(productRepository.save(product));
                })
                .orElse(ResponseEntity.<Void>notFound().build());
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();

            // 删除商品关联的图片文件
            deleteProductImages(product);

            productRepository.delete(product);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
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
}
