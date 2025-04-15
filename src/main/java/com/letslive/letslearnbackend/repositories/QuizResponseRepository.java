package com.letslive.letslearnbackend.repositories;

import com.letslive.letslearnbackend.entities.QuizResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuizResponseRepository extends JpaRepository<QuizResponse, UUID> {
    List<QuizResponse> findAllByTopicId(UUID quizId);
    List<QuizResponse> findAllByStudentId(UUID quizId);
    Optional<QuizResponse> findByTopicIdAndStudentId(UUID quizId, UUID studentId);
}

