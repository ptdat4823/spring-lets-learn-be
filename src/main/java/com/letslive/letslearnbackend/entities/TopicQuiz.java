package com.letslive.letslearnbackend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TopicQuiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @Column(nullable = false, name = "topic_id")
    private UUID topicId;

    @JsonProperty("description")
    private String description;

    @JsonProperty("open")
    private LocalDateTime open;

    @JsonProperty("close")
    private LocalDateTime close;

    @JsonProperty("timeLimit")
    private Number timeLimit;

    @JsonProperty("timeLimitUnit")
    private String timeLimitUnit;

    @JsonProperty("gradeToPass")
    private Number gradeToPass;

    @JsonProperty("gradingMethod")
    private String gradingMethod;

    @JsonProperty("attemptAllowed")
    private String attemptAllowed;

    //@Column(columnDefinition = "jsonb")
    //@Convert(converter = JsonbConverter.class)
    @OneToMany
    @JoinColumn(name = "topic_quiz_id", referencedColumnName = "id")
    @JsonProperty("questions")
    private List<TopicQuizQuestion> questions;
}


