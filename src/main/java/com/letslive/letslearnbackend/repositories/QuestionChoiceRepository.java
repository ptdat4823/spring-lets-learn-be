package com.letslive.letslearnbackend.repositories;

import com.letslive.letslearnbackend.entities.QuestionChoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface QuestionChoiceRepository extends JpaRepository<QuestionChoice, UUID> { }
