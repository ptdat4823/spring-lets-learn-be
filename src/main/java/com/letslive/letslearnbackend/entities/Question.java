package com.letslive.letslearnbackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
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
    
    @Column(columnDefinition = "jsonb")
    private String data;

    @OneToMany
    @JoinColumn(name = "question_id", referencedColumnName = "id")
    private Set<QuestionChoice> choices;

    @ManyToOne
    @JoinColumn(name = "created_by", referencedColumnName = "id")
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "modified_by", referencedColumnName = "id")
    private User modifiedBy;
}