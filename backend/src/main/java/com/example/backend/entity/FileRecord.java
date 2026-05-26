package com.example.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "file_record")
public class FileRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "原始文件名不能为空")
    @Column(name = "original_name", nullable = false)
    private String originalName;

    @NotBlank(message = "存储文件名不能为空")
    @Column(name = "stored_name", nullable = false)
    private String storedName;

    @NotBlank(message = "文件路径不能为空")
    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;

    @NotNull(message = "文件大小不能为空")
    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @NotBlank(message = "文件类型不能为空")
    @Column(name = "file_type", nullable = false, length = 50)
    private String fileType;

    @Column(name = "entity_type", length = 50)
    private String entityType;

    @Column(name = "entity_id")
    private Long entityId;

    @NotNull(message = "上传者ID不能为空")
    @Column(name = "uploader_id", nullable = false)
    private Long uploaderId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
