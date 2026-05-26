package com.example.backend.dto;

import com.example.backend.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    
    private Long id;
    private String name;
    private String icon;
    private String color;
    private Integer sortOrder;
    private Long userId;
    private LocalDateTime createdAt;
    
    public static CategoryDTO fromEntity(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .icon(category.getIcon())
                .color(category.getColor())
                .sortOrder(category.getSortOrder())
                .userId(category.getUser() != null ? category.getUser().getId() : null)
                .createdAt(category.getCreatedAt())
                .build();
    }
}
