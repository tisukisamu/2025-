package com.example.backend.dto;

import com.example.backend.entity.CheckRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckDTO {
    
    private Long id;
    private Long habitId;
    private String habitName;
    private Long userId;
    private LocalDate checkDate;
    private LocalTime checkTime;
    private String note;
    private LocalDateTime createdAt;
    
    public static CheckDTO fromEntity(CheckRecord record) {
        return CheckDTO.builder()
                .id(record.getId())
                .habitId(record.getHabit().getId())
                .habitName(record.getHabit().getName())
                .userId(record.getUser().getId())
                .checkDate(record.getCheckDate())
                .checkTime(record.getCheckTime())
                .note(record.getNote())
                .createdAt(record.getCreatedAt())
                .build();
    }
}
