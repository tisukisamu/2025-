package com.example.backend.service;

import com.example.backend.dto.request.ProductRequest;
import com.example.backend.dto.response.PageResponse;
import com.example.backend.dto.response.ProductDTO;
import com.example.backend.entity.Category;
import com.example.backend.entity.Product;
import com.example.backend.entity.ProductImage;
import com.example.backend.entity.User;
import com.example.backend.exception.BusinessException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.CategoryRepository;
import com.example.backend.repository.ProductImageRepository;
import com.example.backend.repository.ProductRepository;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public PageResponse<ProductDTO> getProducts(String keyword, Long categoryId,
                                                  Double minPrice, Double maxPrice,
                                                  String tradeType, Long sellerId, Product.ProductStatus status,
                                                  String sortBy, String sortOrder,
                                                  int page, int size) {
        Sort sort = Sort.by(
                sortOrder.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                sortBy != null ? sortBy : "createTime"
        );
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        Page<Product> productPage = productRepository.searchProducts(
                keyword, categoryId, minPrice, maxPrice, tradeType, sellerId,
                status != null ? status : Product.ProductStatus.ON_SALE, 
                Product.AuditStatus.APPROVED,
                pageable
        );

        Page<ProductDTO> dtoPage = productPage.map(ProductDTO::fromEntity);
        return PageResponse.of(dtoPage);
    }

    public ProductDTO getProductDetail(Long id) {
        Product product = productRepository.findByIdWithSellerAndImages(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        product.setViewCount(product.getViewCount() + 1);
        productRepository.save(product);

        return ProductDTO.fromEntity(product);
    }

    @Transactional
    public ProductDTO createProduct(Long sellerId, ProductRequest request) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", sellerId));

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
        }

        Product product = new Product();
        product.setTitle(request.getTitle());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setOriginalPrice(request.getOriginalPrice());
        product.setCategory(category);
        product.setSeller(seller);
        product.setStatus(Product.ProductStatus.PENDING);
        product.setAuditStatus(Product.AuditStatus.PENDING);
        product.setTradeType(request.getTradeType());

        Product savedProduct = productRepository.save(product);

        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            List<ProductImage> images = new ArrayList<>();
            for (int i = 0; i < request.getImageUrls().size(); i++) {
                ProductImage image = new ProductImage();
                image.setProduct(savedProduct);
                image.setImageUrl(request.getImageUrls().get(i));
                image.setSortOrder(i);
                images.add(image);
            }
            productImageRepository.saveAll(images);
            savedProduct.setImages(images);
        }

        return ProductDTO.fromEntity(savedProduct);
    }

    @Transactional
    public ProductDTO updateProduct(Long id, Long userId, ProductRequest request) {
        Product product = productRepository.findByIdWithSellerAndImages(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        if (!product.getSeller().getId().equals(userId)) {
            throw new BusinessException("无权修改该商品");
        }

        if (product.getStatus() == Product.ProductStatus.SOLD) {
            throw new BusinessException("已售商品无法修改");
        }

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
        }

        product.setTitle(request.getTitle());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setOriginalPrice(request.getOriginalPrice());
        product.setCategory(category);
        product.setTradeType(request.getTradeType());
        product.setStatus(Product.ProductStatus.PENDING);
        product.setAuditStatus(Product.AuditStatus.PENDING);

        product.getImages().clear();

        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            for (int i = 0; i < request.getImageUrls().size(); i++) {
                ProductImage image = new ProductImage();
                image.setProduct(product);
                image.setImageUrl(request.getImageUrls().get(i));
                image.setSortOrder(i);
                product.getImages().add(image);
            }
        }

        Product updatedProduct = productRepository.save(product);
        return ProductDTO.fromEntity(updatedProduct);
    }

    @Transactional
    public void deleteProduct(Long id, Long userId) {
        Product product = productRepository.findByIdWithSeller(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        if (!product.getSeller().getId().equals(userId)) {
            throw new BusinessException("无权删除该商品");
        }

        if (product.getStatus() == Product.ProductStatus.SOLD) {
            throw new BusinessException("已售商品无法删除");
        }

        productRepository.delete(product);
    }

    @Transactional
    public void updateProductStatus(Long id, Long userId, Product.ProductStatus status) {
        Product product = productRepository.findByIdWithSeller(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        if (!product.getSeller().getId().equals(userId)) {
            throw new BusinessException("无权操作该商品");
        }

        if (product.getStatus() == Product.ProductStatus.SOLD) {
            throw new BusinessException("已售商品无法修改状态");
        }

        product.setStatus(status);
        productRepository.save(product);
    }

    public PageResponse<ProductDTO> getMyProducts(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Product> productPage = productRepository.findBySellerId(userId, pageable);
        Page<ProductDTO> dtoPage = productPage.map(ProductDTO::fromEntity);
        return PageResponse.of(dtoPage);
    }
}
