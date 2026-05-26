package com.example.backend.dto;

import com.example.backend.entity.AlumniProfile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlumniProfileDTO {
    private Long userId;
    private String userName;
    private String userAvatar;
    private String school;
    private String major;
    private Integer graduationYear;
    private String city;
    private Double latitude;
    private Double longitude;
    private String bio;
    private Boolean openNearby;
    private Double distanceKm;

    public static AlumniProfileDTO fromEntity(AlumniProfile profile, Double distanceKm) {
        return AlumniProfileDTO.builder()
                .userId(profile.getUser().getId())
                .userName(profile.getUser().getName())
                .userAvatar(profile.getUser().getAvatar())
                .school(profile.getSchool())
                .major(profile.getMajor())
                .graduationYear(profile.getGraduationYear())
                .city(profile.getCity())
                .latitude(profile.getLatitude())
                .longitude(profile.getLongitude())
                .bio(profile.getBio())
                .openNearby(profile.getOpenNearby())
                .distanceKm(distanceKm)
                .build();
    }
}
