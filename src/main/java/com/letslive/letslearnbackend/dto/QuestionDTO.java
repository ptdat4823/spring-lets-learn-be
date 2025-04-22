package com.letslive.letslearnbackend.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class QuestionDTO {
    private UUID id;
    private String questionName;
    private String questionText;
    private String status;
    private String type;
    private Double defaultMark;
    private Long usage;
    private String feedbackOfTrue;
    private String feedbackOfFalse;
    private Boolean correctAnswer; // for true false question
    private Boolean multiple; // for multiple choices questions

    private List<QuestionChoiceDTO> choices; // for choices and short answer question

    private UserDTO createdBy;
    private UserDTO modifiedBy;
    private CourseDTO course;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
