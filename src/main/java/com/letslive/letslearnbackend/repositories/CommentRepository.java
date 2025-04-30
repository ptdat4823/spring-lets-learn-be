package com.letslive.letslearnbackend.repositories;

import com.letslive.letslearnbackend.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByTopicId(UUID topicId);
}
