package com.club.fund.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ActivityCreateRequest {

    @NotNull(message = "社团ID不能为空")
    private Long clubId;

    @NotBlank(message = "活动名称不能为空")
    private String activityName;

    private String description;

    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;

    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;

    private String location;

    private BigDecimal budget;

    private String coverImage;
}
