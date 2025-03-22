package com.letslive.letslearnbackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TopicQuizQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String questionName;
    private String questionText;
    private String status;
    private String type;
    private BigDecimal defaultMark;
    private String feedbackOfTrue;
    private String feedbackOfFalse;
    private boolean correctAnswer; // for true false question
    private boolean multiple; // for multiple choices questions

    @OneToMany
    @JoinColumn(name = "quiz_question_id", referencedColumnName = "id")
    private Set<TopicQuizQuestionChoice> choices; // for choices and short answer question
}
