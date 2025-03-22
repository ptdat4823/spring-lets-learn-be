package com.letslive.letslearnbackend.repositories;

import com.letslive.letslearnbackend.entities.TopicAssigment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TopicAssigmentRepository extends JpaRepository<TopicAssigment, Long> {
    TopicAssigment findByTopicId(UUID topicId);
}
