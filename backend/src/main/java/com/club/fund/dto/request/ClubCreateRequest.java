package com.club.fund.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClubCreateRequest {

    @NotBlank(message = "社团名称不能为空")
    @Size(max = 100, message = "社团名称不能超过100字符")
    private String clubName;

    @NotBlank(message = "社团编码不能为空")
    @Size(max = 20, message = "社团编码不能超过20字符")
    private String clubCode;

    private String description;

    private String logo;

    private String category;

    private Long presidentId;

    private Long teacherId;
}
