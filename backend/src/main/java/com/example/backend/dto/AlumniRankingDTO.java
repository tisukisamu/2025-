package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlumniRankingDTO {
    private Integer rank;
    private Long userId;
    private String userName;
    private String userAvatar;
    private String school;
    private String city;
    private Double distanceKm;
    private Long totalChecks;
    private Double avgRate;
    private Double score;
}
