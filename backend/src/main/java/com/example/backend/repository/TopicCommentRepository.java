package com.example.backend.repository;

import com.example.backend.entity.TopicComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TopicCommentRepository extends JpaRepository<TopicComment, Long> {

    @Query(value = "SELECT c FROM TopicComment c JOIN FETCH c.user WHERE c.topic.id = :topicId AND c.parent IS NULL AND c.status = :status ORDER BY c.createTime DESC",
            countQuery = "SELECT COUNT(c) FROM TopicComment c WHERE c.topic.id = :topicId AND c.parent IS NULL AND c.status = :status")
    Page<TopicComment> findByTopicIdAndStatusWithUser(@Param("topicId") Long topicId, @Param("status") TopicComment.CommentStatus status, Pageable pageable);

    @Query("SELECT c FROM TopicComment c JOIN FETCH c.user WHERE c.parent.id = :parentId AND c.status = :status ORDER BY c.createTime ASC")
    List<TopicComment> findRepliesByParentId(@Param("parentId") Long parentId, @Param("status") TopicComment.CommentStatus status);

    @Query("SELECT c FROM TopicComment c JOIN FETCH c.user WHERE c.id = :id")
    Optional<TopicComment> findByIdWithUser(@Param("id") Long id);

    @Modifying
    @Query("UPDATE TopicComment c SET c.likeCount = c.likeCount + 1 WHERE c.id = :id")
    void incrementLikeCount(@Param("id") Long id);

    List<TopicComment> findByTopicIdOrderByCreateTimeDesc(Long topicId);
}
