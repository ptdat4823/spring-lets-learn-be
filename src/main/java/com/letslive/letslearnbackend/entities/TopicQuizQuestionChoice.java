package com.letslive.letslearnbackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TopicQuizQuestionChoice {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    private String text;
    private BigDecimal gradePercent;
    private String feedback;

    @Column(name = "quiz_question_id")
    private Long questionId;
}
