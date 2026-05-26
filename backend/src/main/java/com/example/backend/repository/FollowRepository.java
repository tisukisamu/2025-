package com.example.backend.repository;

import com.example.backend.entity.Follow;
import com.example.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    Optional<Follow> findByFollowerIdAndFollowingId(Long followerId, Long followingId);

    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);

    void deleteByFollowerIdAndFollowingId(Long followerId, Long followingId);

    Page<Follow> findByFollowerIdOrderByCreateTimeDesc(Long followerId, Pageable pageable);

    Page<Follow> findByFollowingIdOrderByCreateTimeDesc(Long followingId, Pageable pageable);

    long countByFollowerId(Long followerId);

    long countByFollowingId(Long followingId);

    @Query("SELECT f.following FROM Follow f WHERE f.follower.id = :followerId")
    Page<User> findFollowingByFollowerId(@Param("followerId") Long followerId, Pageable pageable);

    @Query("SELECT f.follower FROM Follow f WHERE f.following.id = :followingId")
    Page<User> findFollowersByFollowingId(@Param("followingId") Long followingId, Pageable pageable);
}
