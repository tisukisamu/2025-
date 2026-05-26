package com.example.backend.dto;

import com.example.backend.entity.AlumniTeam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlumniTeamDTO {
    private Long id;
    private String name;
    private String slogan;
    private String city;
    private Double latitude;
    private Double longitude;
    private Integer maxMembers;
    private Integer currentMembers;
    private String status;
    private Long ownerId;
    private String ownerName;
    private Double distanceKm;
    private Boolean joined;
    private LocalDateTime createdAt;

    public static AlumniTeamDTO fromEntity(
            AlumniTeam team,
            int currentMembers,
            Double distanceKm,
            boolean joined
    ) {
        return AlumniTeamDTO.builder()
                .id(team.getId())
                .name(team.getName())
                .slogan(team.getSlogan())
                .city(team.getCity())
                .latitude(team.getLatitude())
                .longitude(team.getLongitude())
                .maxMembers(team.getMaxMembers())
                .currentMembers(currentMembers)
                .status(team.getStatus().name())
                .ownerId(team.getOwner().getId())
                .ownerName(team.getOwner().getName())
                .distanceKm(distanceKm)
                .joined(joined)
                .createdAt(team.getCreatedAt())
                .build();
    }
}
