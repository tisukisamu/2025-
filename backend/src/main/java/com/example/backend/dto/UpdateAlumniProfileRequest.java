package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAlumniProfileRequest {
    private String school;
    private String major;
    private Integer graduationYear;
    private String city;
    private Double latitude;
    private Double longitude;
    private String bio;
    private Boolean openNearby;
}
