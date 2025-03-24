package com.letslive.letslearnbackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    private String questionName;
    private String questionText;
    private String status;
    private String type;
    private BigDecimal defaultMark;
    private Long usage;
    private String feedbackOfTrue;
    private String feedbackOfFalse;
    private boolean correctAnswer; // for true false question
    private boolean multiple; // for multiple choices questions

    @OneToMany
    @JoinColumn(name = "question_id", referencedColumnName = "id")
    private List<QuestionChoice> choices; // for choices and short answer question

    @ManyToOne
    @JoinColumn(name = "created_by", referencedColumnName = "id")
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "modified_by", referencedColumnName = "id")
    private User modifiedBy;

    @ManyToOne
    @JoinColumn(name = "course_id", referencedColumnName = "id")
    private Course course;
}