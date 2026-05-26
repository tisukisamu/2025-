package com.example.backend.dto;

import com.example.backend.entity.BuyRequestResponse;
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
public class BuyRequestResponseDTO {
    
    private Long id;
    private Long buyRequestId;
    private Long responderId;
    private String responderName;
    private String responderAvatar;
    private Long productId;
    private String productTitle;
    private String productImage;
    private String message;
    private BigDecimal offeredPrice;
    private BuyRequestResponse.ResponseStatus status;
    private LocalDateTime createTime;
    
    public static BuyRequestResponseDTO fromEntity(BuyRequestResponse response) {
        String productImage = null;
        if (response.getProduct() != null && response.getProduct().getImages() != null 
                && !response.getProduct().getImages().isEmpty()) {
            productImage = response.getProduct().getImages().get(0).getImageUrl();
        }
        
        return BuyRequestResponseDTO.builder()
                .id(response.getId())
                .buyRequestId(response.getBuyRequest() != null ? response.getBuyRequest().getId() : null)
                .responderId(response.getResponder() != null ? response.getResponder().getId() : null)
                .responderName(response.getResponder() != null ? response.getResponder().getUsername() : null)
                .responderAvatar(response.getResponder() != null ? response.getResponder().getAvatar() : null)
                .productId(response.getProduct() != null ? response.getProduct().getId() : null)
                .productTitle(response.getProduct() != null ? response.getProduct().getTitle() : null)
                .productImage(productImage)
                .message(response.getMessage())
                .offeredPrice(response.getOfferedPrice())
                .status(response.getStatus())
                .createTime(response.getCreateTime())
                .build();
    }
}
