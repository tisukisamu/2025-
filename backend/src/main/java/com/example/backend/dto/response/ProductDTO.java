package com.example.backend.dto.response;

import com.example.backend.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Long categoryId;
    private String categoryName;
    private Long sellerId;
    private String sellerName;
    private String sellerAvatar;
    private Product.ProductStatus status;
    private Product.AuditStatus auditStatus;
    private String auditReason;
    private Integer viewCount;
    private Integer favoriteCount;
    private Product.TradeType tradeType;
    private List<String> imageUrls;
    private String coverImage;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public static ProductDTO fromEntity(Product product) {
        String coverImage = null;
        List<String> imageUrls = null;
        
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            coverImage = product.getImages().get(0).getImageUrl();
            imageUrls = product.getImages().stream()
                    .map(img -> img.getImageUrl())
                    .toList();
        }

        return ProductDTO.builder()
                .id(product.getId())
                .title(product.getTitle())
                .description(product.getDescription())
                .price(product.getPrice())
                .originalPrice(product.getOriginalPrice())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .sellerId(product.getSeller().getId())
                .sellerName(product.getSeller().getUsername())
                .sellerAvatar(product.getSeller().getAvatar())
                .status(product.getStatus())
                .auditStatus(product.getAuditStatus())
                .auditReason(product.getAuditReason())
                .viewCount(product.getViewCount())
                .favoriteCount(product.getFavoriteCount())
                .tradeType(product.getTradeType())
                .imageUrls(imageUrls)
                .coverImage(coverImage)
                .createTime(product.getCreateTime())
                .updateTime(product.getUpdateTime())
                .build();
    }
}
