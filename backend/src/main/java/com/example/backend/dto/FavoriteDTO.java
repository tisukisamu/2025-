package com.example.backend.dto;

import com.example.backend.entity.Favorite;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteDTO {
    
    private Long id;
    private Long productId;
    private String productTitle;
    private String productCoverImage;
    private BigDecimal productPrice;
    private String productStatus;
    private LocalDateTime createTime;
    
    public static FavoriteDTO fromEntity(Favorite favorite) {
        String coverImage = null;
        if (favorite.getProduct() != null && favorite.getProduct().getImages() != null 
                && !favorite.getProduct().getImages().isEmpty()) {
            coverImage = favorite.getProduct().getImages().get(0).getImageUrl();
        }
        
        return FavoriteDTO.builder()
                .id(favorite.getId())
                .productId(favorite.getProduct() != null ? favorite.getProduct().getId() : null)
                .productTitle(favorite.getProduct() != null ? favorite.getProduct().getTitle() : null)
                .productCoverImage(coverImage)
                .productPrice(favorite.getProduct() != null ? favorite.getProduct().getPrice() : null)
                .productStatus(favorite.getProduct() != null ? favorite.getProduct().getStatus().name() : null)
                .createTime(favorite.getCreateTime())
                .build();
    }
}
