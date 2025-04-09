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
    private UserDTO student; // no need when uploading
    private UUID topicId; // no need when uploading
    private String status;
    private String startedAt;
    private String completedAt;
    private List<QuizResponseAnswerDTO> answers;
}