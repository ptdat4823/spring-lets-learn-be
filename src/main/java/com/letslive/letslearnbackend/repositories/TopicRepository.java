package com.letslive.letslearnbackend.repositories;

import com.letslive.letslearnbackend.entities.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TopicRepository extends JpaRepository<Topic, UUID> {
    List<Topic> findAllBySectionId(UUID sectionId);
}
