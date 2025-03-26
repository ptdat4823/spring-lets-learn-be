package com.letslive.letslearnbackend.repositories;

import com.letslive.letslearnbackend.entities.TopicQuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TopicQuizQuestionRepository extends JpaRepository<TopicQuizQuestion, Long> {
    List<TopicQuizQuestion> findAllByTopicQuizId(Long topicId);
}
