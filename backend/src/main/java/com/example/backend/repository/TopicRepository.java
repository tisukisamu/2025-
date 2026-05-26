package com.example.backend.repository;

import com.example.backend.entity.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {

    @Query(value = "SELECT t FROM Topic t JOIN FETCH t.author WHERE t.status = :status ORDER BY t.isPinned DESC, t.createTime DESC",
            countQuery = "SELECT COUNT(t) FROM Topic t WHERE t.status = :status")
    Page<Topic> findByStatusWithAuthor(@Param("status") Topic.TopicStatus status, Pageable pageable);

    @Query(value = "SELECT t FROM Topic t JOIN FETCH t.author WHERE t.category = :category AND t.status = :status ORDER BY t.isPinned DESC, t.createTime DESC",
            countQuery = "SELECT COUNT(t) FROM Topic t WHERE t.category = :category AND t.status = :status")
    Page<Topic> findByCategoryAndStatus(@Param("category") String category, @Param("status") Topic.TopicStatus status, Pageable pageable);

    @Query("SELECT t FROM Topic t JOIN FETCH t.author WHERE t.id = :id")
    Optional<Topic> findByIdWithAuthor(@Param("id") Long id);

    @Query(value = "SELECT t FROM Topic t JOIN FETCH t.author WHERE t.author.id = :authorId ORDER BY t.createTime DESC",
            countQuery = "SELECT COUNT(t) FROM Topic t WHERE t.author.id = :authorId")
    Page<Topic> findByAuthorIdWithAuthor(@Param("authorId") Long authorId, Pageable pageable);

    @Query("SELECT t FROM Topic t JOIN FETCH t.author WHERE t.isHot = true AND t.status = :status ORDER BY t.likeCount DESC")
    Page<Topic> findHotTopics(@Param("status") Topic.TopicStatus status, Pageable pageable);

    @Modifying
    @Query("UPDATE Topic t SET t.viewCount = t.viewCount + 1 WHERE t.id = :id")
    void incrementViewCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Topic t SET t.likeCount = t.likeCount + 1 WHERE t.id = :id")
    void incrementLikeCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Topic t SET t.likeCount = t.likeCount - 1 WHERE t.id = :id AND t.likeCount > 0")
    void decrementLikeCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Topic t SET t.commentCount = t.commentCount + 1 WHERE t.id = :id")
    void incrementCommentCount(@Param("id") Long id);

    long countByStatus(Topic.TopicStatus status);
}
