package com.letslive.letslearnbackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
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
class QuestionChoice {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;
    
    private String text;
    private BigDecimal gradePercent;
    private String feedback;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;
}