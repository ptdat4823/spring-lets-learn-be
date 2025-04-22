package com.letslive.letslearnbackend.repositories;

import com.letslive.letslearnbackend.entities.TopicPage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TopicPageRepository extends JpaRepository<TopicPage, UUID> {
    Optional<TopicPage> findByTopicId(UUID topicId);
}
