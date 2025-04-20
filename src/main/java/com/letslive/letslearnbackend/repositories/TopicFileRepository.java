package com.letslive.letslearnbackend.repositories;

import com.letslive.letslearnbackend.entities.TopicFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TopicFileRepository extends JpaRepository<TopicFile, UUID> { }
