package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "topic_comments")
public class TopicComment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private TopicComment parent;
    
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<TopicComment> replies = new ArrayList<>();
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "like_count")
    private Integer likeCount = 0;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private CommentStatus status = CommentStatus.NORMAL;
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createTime;
    
    public enum CommentStatus {
        NORMAL,
        DELETED
    }
}
