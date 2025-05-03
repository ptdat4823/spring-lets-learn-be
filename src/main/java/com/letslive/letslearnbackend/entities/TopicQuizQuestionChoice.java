package com.letslive.letslearnbackend.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("id")
    private UUID id;

    @JsonProperty("text")
    private String text;

    @JsonProperty("gradePercent")
    private BigDecimal gradePercent;

    @JsonProperty("feedback")
    private String feedback;

    @Column(name = "quiz_question_id", nullable = true)
    private UUID questionId;
}
