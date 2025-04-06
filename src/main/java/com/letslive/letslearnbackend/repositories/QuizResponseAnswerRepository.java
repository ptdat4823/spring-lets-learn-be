package com.letslive.letslearnbackend.repositories;

import com.letslive.letslearnbackend.entities.QuizResponseAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface QuizResponseAnswerRepository extends JpaRepository<QuizResponseAnswer, UUID> { }