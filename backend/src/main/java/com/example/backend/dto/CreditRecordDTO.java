package com.example.backend.dto;

import com.example.backend.entity.CreditRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditRecordDTO {
    
    private Long id;
    private Long userId;
    private String userName;
    private CreditRecord.CreditType type;
    private Integer points;
    private String description;
    private Long relatedId;
    private String relatedType;
    private LocalDateTime createTime;
    
    public static CreditRecordDTO fromEntity(CreditRecord record) {
        return CreditRecordDTO.builder()
                .id(record.getId())
                .userId(record.getUser() != null ? record.getUser().getId() : null)
                .userName(record.getUser() != null ? record.getUser().getUsername() : null)
                .type(record.getType())
                .points(record.getPoints())
                .description(record.getDescription())
                .relatedId(record.getRelatedId())
                .relatedType(record.getRelatedType())
                .createTime(record.getCreateTime())
                .build();
    }
}
