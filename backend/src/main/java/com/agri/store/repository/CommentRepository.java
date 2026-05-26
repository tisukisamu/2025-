package com.agri.store.repository;

import com.agri.store.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByProductId(Long productId);
    List<Comment> findByUserId(Long userId);
    List<Comment> findByProductIdOrderByCreateTimeDesc(Long productId);
}
