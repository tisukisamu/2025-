package com.example.backend.dto;

import com.example.backend.entity.AlumniTeamMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlumniTeamMemberDTO {
    private Long userId;
    private String userName;
    private String userAvatar;
    private String role;
    private Long totalChecks;
    private Double avgRate;

    public static AlumniTeamMemberDTO fromEntity(AlumniTeamMember member, Long totalChecks, Double avgRate) {
        return AlumniTeamMemberDTO.builder()
                .userId(member.getUser().getId())
                .userName(member.getUser().getName())
                .userAvatar(member.getUser().getAvatar())
                .role(member.getRole().name())
                .totalChecks(totalChecks)
                .avgRate(avgRate)
                .build();
    }
}
