package com.letslive.letslearnbackend.mappers;

import com.letslive.letslearnbackend.dto.QuizResponseDTO;
import com.letslive.letslearnbackend.entities.QuizResponse;

public class QuizResponseMapper {
    public static QuizResponse toEntity(QuizResponseDTO quizResponseDTO) {
        QuizResponse.QuizResponseBuilder builder = QuizResponse.builder();
        builder
                .id(quizResponseDTO.getId())
                .completedAt(quizResponseDTO.getCompletedAt())
                .status(quizResponseDTO.getStatus())
                .topicId(quizResponseDTO.getTopicId())
                .answers(quizResponseDTO.getAnswers().stream().map(QuizResponseAnswerMapper::toEntity).toList())
                .startedAt(quizResponseDTO.getStartedAt())
                .student(UserMapper.mapToEntity(quizResponseDTO.getStudent()));

        return builder.build();
    }

    public static QuizResponseDTO toDto(QuizResponse quizResponse) {
        QuizResponseDTO.QuizResponseDTOBuilder builder = QuizResponseDTO.builder();
        builder.id(quizResponse.getId())
                .completedAt(quizResponse.getCompletedAt())
                .status(quizResponse.getStatus())
                .topicId(quizResponse.getTopicId())
                .answers(quizResponse.getAnswers().stream().map(QuizResponseAnswerMapper::toDto).toList())
                .student(UserMapper.mapToDTO(quizResponse.getStudent()));
        return builder.build();
    }
}
