package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlumniTeamDetailDTO {
    private AlumniTeamDTO team;
    private List<AlumniTeamMemberDTO> members;
    private List<AlumniTeamMessageDTO> messages;
}
