package com.letslive.letslearnbackend.dto;

import com.letslive.letslearnbackend.entities.Course;
import com.letslive.letslearnbackend.entities.User;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
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
    private BigDecimal defaultMark;
    private Long usage;
    private String feedbackOfTrue;
    private String feedbackOfFalse;
    private Boolean correctAnswer; // for true false question
    private Boolean multiple; // for multiple choices questions

    private List<QuestionChoiceDTO> choices; // for choices and short answer question

    private UserDTO createdBy;
    private UserDTO modifiedBy;
    //private CourseDTO course;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
