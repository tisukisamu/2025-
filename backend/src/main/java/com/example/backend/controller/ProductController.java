package com.example.backend.controller;

import com.example.backend.dto.request.ProductRequest;
import com.example.backend.dto.response.ApiResponse;
import com.example.backend.dto.response.PageResponse;
import com.example.backend.dto.response.ProductDTO;
import com.example.backend.entity.Product;
import com.example.backend.security.UserDetailsImpl;
import com.example.backend.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProductDTO>>> getProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String tradeType,
            @RequestParam(required = false) Long sellerId,
            @RequestParam(required = false) Product.ProductStatus status,
            @RequestParam(defaultValue = "createTime") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int size) {
        
        PageResponse<ProductDTO> products = productService.getProducts(
                keyword, categoryId, minPrice, maxPrice, tradeType, sellerId, status, sortBy, sortOrder, page, size
        );
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDTO>> getProductDetail(@PathVariable Long id) {
        ProductDTO product = productService.getProductDetail(id);
        return ResponseEntity.ok(ApiResponse.success(product));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductDTO>> createProduct(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody ProductRequest request) {
        ProductDTO product = productService.createProduct(userDetails.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("商品发布成功，等待审核", product));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDTO>> updateProduct(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody ProductRequest request) {
        ProductDTO product = productService.updateProduct(id, userDetails.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("商品更新成功", product));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        productService.deleteProduct(id, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("商品删除成功", null));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Void>> updateProductStatus(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam Product.ProductStatus status) {
        productService.updateProductStatus(id, userDetails.getId(), status);
        return ResponseEntity.ok(ApiResponse.success("状态更新成功", null));
    }

    @GetMapping("/mine")
    public ResponseEntity<ApiResponse<PageResponse<ProductDTO>>> getMyProducts(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<ProductDTO> products = productService.getMyProducts(userDetails.getId(), page, size);
        return ResponseEntity.ok(ApiResponse.success(products));
    }
}
