package com.example.backend.dto.response;

import com.example.backend.entity.Order;
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
public class OrderDTO {

    private Long id;
    private String orderNo;
    private Long buyerId;
    private String buyerName;
    private String buyerAvatar;
    private Long sellerId;
    private String sellerName;
    private String sellerAvatar;
    private Long productId;
    private String productTitle;
    private String productImage;
    private Order.OrderStatus status;
    private Order.TradeType tradeType;
    private BigDecimal amount;
    private String address;
    private String expressNo;
    private String cancelReason;
    private LocalDateTime shipTime;
    private LocalDateTime completeTime;
    private LocalDateTime createTime;

    public static OrderDTO fromEntity(Order order) {
        String productImage = null;
        if (order.getProduct().getImages() != null && !order.getProduct().getImages().isEmpty()) {
            productImage = order.getProduct().getImages().get(0).getImageUrl();
        }

        return OrderDTO.builder()
                .id(order.getId())
                .orderNo(order.getOrderNo())
                .buyerId(order.getBuyer().getId())
                .buyerName(order.getBuyer().getUsername())
                .buyerAvatar(order.getBuyer().getAvatar())
                .sellerId(order.getSeller().getId())
                .sellerName(order.getSeller().getUsername())
                .sellerAvatar(order.getSeller().getAvatar())
                .productId(order.getProduct().getId())
                .productTitle(order.getProduct().getTitle())
                .productImage(productImage)
                .status(order.getStatus())
                .tradeType(order.getTradeType())
                .amount(order.getAmount())
                .address(order.getAddress())
                .expressNo(order.getExpressNo())
                .cancelReason(order.getCancelReason())
                .shipTime(order.getShipTime())
                .completeTime(order.getCompleteTime())
                .createTime(order.getCreateTime())
                .build();
    }
}
