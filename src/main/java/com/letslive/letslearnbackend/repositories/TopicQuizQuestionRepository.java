package com.letslive.letslearnbackend.repositories;

import com.letslive.letslearnbackend.entities.TopicQuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TopicQuizQuestionRepository extends JpaRepository<TopicQuizQuestion, UUID> {
    List<TopicQuizQuestion> findAllByTopicQuizId(UUID topicId);
}
