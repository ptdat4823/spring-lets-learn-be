package com.letslive.letslearnbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class QuizResponseDTO {
    private UUID id;
    private UserDTO user; // no need when uploading
    private UUID topicId;
    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private List<QuizResponseAnswerDTO> answers;
}