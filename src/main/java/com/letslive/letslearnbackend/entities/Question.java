package com.letslive.letslearnbackend.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Question {
    @Id
    @GeneratedValue(generator = "UUID")
    @JsonProperty
    private UUID id;

    @JsonProperty
    private LocalDateTime createdAt;

    @JsonProperty
    private LocalDateTime updatedAt;

    @JsonProperty
    private LocalDateTime deletedAt;


    @JsonProperty
    private String questionName;

    @JsonProperty
    private String questionText;
    @JsonProperty
    private String status;
    @JsonProperty
    private String type;
    @JsonProperty
    private Double defaultMark;
    @JsonProperty
    private Long usage;
    @JsonProperty
    private String feedbackOfTrue;
    @JsonProperty
    private String feedbackOfFalse;
    @JsonProperty
    private boolean correctAnswer; // for true false question
    @JsonProperty
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