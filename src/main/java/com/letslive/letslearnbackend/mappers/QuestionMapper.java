package com.letslive.letslearnbackend.mappers;

import com.letslive.letslearnbackend.dto.QuestionDTO;
import com.letslive.letslearnbackend.entities.Question;
import com.letslive.letslearnbackend.entities.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuestionMapper {
    public static QuestionDTO toDTO(Question question) {
        QuestionDTO.QuestionDTOBuilder builder = QuestionDTO.builder();
        builder
                .id(question.getId())
                .questionName(question.getQuestionName())
                .questionText(question.getQuestionText())
                .type(question.getType())
                .defaultMark(question.getDefaultMark())
                .usage(question.getUsage())
                .feedbackOfFalse(question.getFeedbackOfFalse())
                .feedbackOfTrue(question.getFeedbackOfTrue())
                .choices(question.getChoices().stream().map(choice -> QuestionChoiceMapper.toDTO(choice)).toList())
                .correctAnswer(question.isCorrectAnswer())
                .multiple(question.isMultiple())
                .createdBy(UserMapper.mapToDTO(question.getCreatedBy()))
                .modifiedBy(UserMapper.mapToDTO(question.getModifiedBy()))
                .updatedAt(question.getUpdatedAt())
                .createdAt(question.getCreatedAt())
                .deletedAt(question.getDeletedAt());

        return builder.build();
    }

    public static Question toEntity(QuestionDTO questionDTO) {
        Question.QuestionBuilder builder = Question.builder();
        builder
                .id(questionDTO.getId())
                .questionName(questionDTO.getQuestionName())
                .questionText(questionDTO.getQuestionText())
                .type(questionDTO.getType())
                .defaultMark(questionDTO.getDefaultMark())
                .usage(questionDTO.getUsage())
                .feedbackOfFalse(questionDTO.getFeedbackOfFalse())
                .feedbackOfTrue(questionDTO.getFeedbackOfTrue())
                .choices(questionDTO.getChoices().stream().map(choice -> QuestionChoiceMapper.toEntity(choice)).toList())
                .correctAnswer(questionDTO.isCorrectAnswer())
                .multiple(questionDTO.isMultiple())
                .createdBy(UserMapper.mapToEntity(questionDTO.getCreatedBy()))
                .modifiedBy(UserMapper.mapToEntity(questionDTO.getModifiedBy()))
                .updatedAt(questionDTO.getUpdatedAt())
                .createdAt(questionDTO.getCreatedAt())
                .deletedAt(questionDTO.getDeletedAt());

        return builder.build();
    }
}