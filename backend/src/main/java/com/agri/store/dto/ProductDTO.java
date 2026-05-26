package com.agri.store.dto;

import com.agri.store.entity.Product;
import com.agri.store.util.ImageUrlUtil;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Product数据传输对象
 * 包含图片路径双保底策略的响应字段
 */
@Data
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private Integer stockWarning;
    
    // 原始字段保留
    private String imageUrl;
    private String imageUrls;
    
    // 图片路径双保底策略字段
    private String primaryImageUrl;
    private String primaryImageType;
    private String fallbackImageUrl;
    private List<String> allImageUrls;
    private String imagePathType;
    private String imagePathTypeDesc;
    
    private Boolean active;
    private String category;
    private Long categoryId;
    private Boolean isNew;
    private Boolean isHot;
    private Integer sales;
    private Long storeId;
    private Integer status;
    private String rejectReason;
    private LocalDateTime auditTime;
    private Long auditBy;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    /**
     * 从实体转换为DTO
     *
     * @param product 产品实体
     * @return ProductDTO
     */
    public static ProductDTO fromEntity(Product product) {
        if (product == null) {
            return null;
        }

        ProductDTO dto = new ProductDTO();
        
        // 基础字段
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        dto.setStockWarning(product.getStockWarning());
        
        // 保留原始图片字段
        dto.setImageUrl(product.getImageUrl());
        dto.setImageUrls(product.getImageUrls());
        
        // 应用双保底策略
        ImageUrlUtil.ImagePathInfo imageInfo = ImageUrlUtil.buildImagePathInfo(product);
        dto.setPrimaryImageUrl(imageInfo.getPrimaryUrl());
        dto.setPrimaryImageType(imageInfo.getPrimaryType());
        dto.setFallbackImageUrl(imageInfo.getFallbackUrl());
        dto.setAllImageUrls(imageInfo.getAllUrls());
        dto.setImagePathType(imageInfo.getPathType());
        dto.setImagePathTypeDesc(imageInfo.getPathTypeDesc());
        
        // 其他字段
        dto.setActive(product.getActive());
        dto.setCategory(product.getCategory());
        dto.setCategoryId(product.getCategoryId());
        dto.setIsNew(product.getIsNew());
        dto.setIsHot(product.getIsHot());
        dto.setSales(product.getSales());
        dto.setStoreId(product.getStoreId());
        dto.setStatus(product.getStatus());
        dto.setRejectReason(product.getRejectReason());
        dto.setAuditTime(product.getAuditTime());
        dto.setAuditBy(product.getAuditBy());
        dto.setCreateTime(product.getCreateTime());
        dto.setUpdateTime(product.getUpdateTime());

        return dto;
    }
}
