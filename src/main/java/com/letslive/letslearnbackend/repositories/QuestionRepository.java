package com.letslive.letslearnbackend.repositories;

import com.letslive.letslearnbackend.entities.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface QuestionRepository extends JpaRepository<Question, UUID> {
    List<Question> findAllByCourseId(UUID courseId);
}
