package com.example.backend.repository;

import com.example.backend.entity.InterviewRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InterviewRoomRepository extends JpaRepository<InterviewRoom, Long> {
    Optional<InterviewRoom> findByRoomId(String roomId);
    Optional<InterviewRoom> findByInterviewId(Long interviewId);
}
