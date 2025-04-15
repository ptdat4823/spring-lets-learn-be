package com.letslive.letslearnbackend.repositories;

import com.letslive.letslearnbackend.entities.TopicQuiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TopicQuizRepository extends JpaRepository<TopicQuiz, UUID> {
    Optional<TopicQuiz> findByTopicId(UUID topicId);
}