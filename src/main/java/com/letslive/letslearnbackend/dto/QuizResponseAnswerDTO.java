package com.letslive.letslearnbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class QuizResponseAnswerDTO {
    private UUID id;
    private String question; // question data as JSON
    private String answer;
    private int mark;
}
