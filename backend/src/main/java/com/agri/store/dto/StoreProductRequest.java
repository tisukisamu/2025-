package com.agri.store.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class StoreProductRequest {
    private Long id;

    @NotBlank(message = "商品名称不能为空")
    @Size(min = 2, max = 200, message = "商品名称长度必须在2-200之间")
    private String name;

    @Size(max = 2000, message = "商品描述长度不能超过2000")
    private String description;

    @NotNull(message = "商品价格不能为空")
    @Positive(message = "商品价格必须大于0")
    private BigDecimal price;

    @NotNull(message = "商品库存不能为空")
    @Positive(message = "商品库存必须大于0")
    private Integer stock;

    private Integer stockWarning = 10;

    // 主图URL（相对路径）
    private String mainImageUrl;

    // 图片URL列表（相对路径），最多4张
    @Size(max = 4, message = "图片最多只能上传4张")
    private List<String> imageUrls;

    @NotNull(message = "商品分类不能为空")
    private Long categoryId;
}
