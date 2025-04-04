package com.letslive.letslearnbackend.repositories;

import com.letslive.letslearnbackend.entities.QuizResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

public interface QuizResponseRepository extends JpaRepository<QuizResponse, UUID> {
    List<QuizResponse> findAllByTopicId(UUID quizId);
}
