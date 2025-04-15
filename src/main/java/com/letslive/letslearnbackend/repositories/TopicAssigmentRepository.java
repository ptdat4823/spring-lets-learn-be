package com.letslive.letslearnbackend.repositories;

import com.letslive.letslearnbackend.entities.TopicAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TopicAssigmentRepository extends JpaRepository<TopicAssignment, UUID> {
    Optional<TopicAssignment> findByTopicId(UUID topicId);
}
