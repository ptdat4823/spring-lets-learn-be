package com.letslive.letslearnbackend.repositories;

import com.letslive.letslearnbackend.entities.TopicQuizQuestionChoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TopicQuizQuestionChoiceRepository extends JpaRepository<TopicQuizQuestionChoice, UUID> { }
