package com.letslive.letslearnbackend.repositories;

import com.letslive.letslearnbackend.entities.Topic;
import com.letslive.letslearnbackend.entities.TopicQuiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TopicQuizRepository extends JpaRepository<TopicQuiz, Long> {
    TopicQuiz findByTopicId(UUID topicId);
}
