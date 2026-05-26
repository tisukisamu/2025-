package com.club.fund.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ActivityResponse {

    private Long id;

    private String activityName;

    private String description;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String location;

    private BigDecimal budget;

    private String status;

    private String coverImage;

    private Integer signupCount;

    private LocalDateTime createTime;

    private ClubResponse club;
}
