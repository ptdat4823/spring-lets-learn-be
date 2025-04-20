package com.letslive.letslearnbackend.repositories;

import com.letslive.letslearnbackend.entities.TopicLink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TopicLinkRepository extends JpaRepository<TopicLink, UUID> {
}
