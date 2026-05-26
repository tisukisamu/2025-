package com.example.backend.dto;

import com.example.backend.entity.Announcement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementDTO {
    
    private Long id;
    private String title;
    private String content;
    private Announcement.AnnouncementType type;
    private Boolean isTop;
    private Long authorId;
    private String authorName;
    private Integer viewCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    public static AnnouncementDTO fromEntity(Announcement announcement) {
        return AnnouncementDTO.builder()
                .id(announcement.getId())
                .title(announcement.getTitle())
                .content(announcement.getContent())
                .type(announcement.getType())
                .isTop(announcement.getIsTop())
                .authorId(announcement.getAuthor() != null ? announcement.getAuthor().getId() : null)
                .authorName(announcement.getAuthor() != null ? announcement.getAuthor().getUsername() : null)
                .viewCount(announcement.getViewCount())
                .createTime(announcement.getCreateTime())
                .updateTime(announcement.getUpdateTime())
                .build();
    }
}
