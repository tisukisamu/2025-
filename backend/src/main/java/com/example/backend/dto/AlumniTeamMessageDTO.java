package com.example.backend.dto;

import com.example.backend.entity.AlumniTeamMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlumniTeamMessageDTO {
    private Long id;
    private Long teamId;
    private Long userId;
    private String userName;
    private String userAvatar;
    private String content;
    private LocalDateTime createdAt;

    public static AlumniTeamMessageDTO fromEntity(AlumniTeamMessage message) {
        return AlumniTeamMessageDTO.builder()
                .id(message.getId())
                .teamId(message.getTeam().getId())
                .userId(message.getUser().getId())
                .userName(message.getUser().getName())
                .userAvatar(message.getUser().getAvatar())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
