package com.letslive.letslearnbackend.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

// QuestionChoice.java
@Entity
@Table(name = "question_choices")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionChoice {
    @Id
    @GeneratedValue(generator = "UUID")
    @JsonProperty
    private UUID id;

    @JsonProperty
    private String text;
    @JsonProperty
    private BigDecimal gradePercent;
    @JsonProperty
    private String feedback;

    @JsonProperty
    @Column(name = "question_id")
    private UUID questionId;
}