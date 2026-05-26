package com.club.fund.repository;

import com.club.fund.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.role WHERE u.username = :username")
    Optional<User> findByUsernameWithRole(@Param("username") String username);

    Optional<User> findByStudentId(String studentId);

    boolean existsByUsername(String username);

    boolean existsByStudentId(String studentId);

    @Query("SELECT u FROM User u WHERE u.deleted = 0")
    Page<User> findAllActive(Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.role.roleCode = :roleCode AND u.deleted = 0")
    List<User> findByRoleCode(@Param("roleCode") String roleCode);

    @Query("SELECT u FROM User u WHERE u.id IN (SELECT cm.user.id FROM ClubMember cm WHERE cm.club.id = :clubId AND cm.status = 1)")
    List<User> findByClubId(@Param("clubId") Long clubId);
}
