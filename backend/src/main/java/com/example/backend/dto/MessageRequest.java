package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MessageRequest {
    
    private Long albumId;
    
    private Long userId;
    
    @NotBlank(message = "留言者姓名不能为空")
    private String authorName;
    
    @NotBlank(message = "留言内容不能为空")
    private String content;
}
