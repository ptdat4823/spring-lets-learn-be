package com.letslive.letslearnbackend.mappers;

import com.letslive.letslearnbackend.dto.QuestionChoiceDTO;
import com.letslive.letslearnbackend.entities.Question;
import com.letslive.letslearnbackend.entities.QuestionChoice;

public class QuestionChoiceMapper {
    public static QuestionChoiceDTO toDTO(QuestionChoice questionChoice) {
        QuestionChoiceDTO.QuestionChoiceDTOBuilder builder = QuestionChoiceDTO
                .builder()
                .id(questionChoice.getId())
                .text(questionChoice.getText())
                .feedback(questionChoice.getFeedback())
                .gradePercent(questionChoice.getGradePercent());

        return builder.build();
    }

    public static QuestionChoice toEntity(QuestionChoiceDTO dto) {
        QuestionChoice.QuestionChoiceBuilder builder = QuestionChoice
                .builder()
                .id(dto.getId())
                .text(dto.getText())
                .feedback(dto.getFeedback())
                .gradePercent(dto.getGradePercent());

        return builder.build();
    }
}
