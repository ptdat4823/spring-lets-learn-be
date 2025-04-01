package com.letslive.letslearnbackend.mappers;

import com.letslive.letslearnbackend.dto.QuizResponseAnswerDTO;
import com.letslive.letslearnbackend.entities.QuizResponseAnswer;

public class QuizResponseAnswerMapper {
    public static QuizResponseAnswer toEntity(QuizResponseAnswerDTO quizResponseAnswerDTO) {
        QuizResponseAnswer.QuizResponseAnswerBuilder quizResponseAnswerBuilder = QuizResponseAnswer.builder();
        quizResponseAnswerBuilder
                .id(quizResponseAnswerDTO.getId())
                .answer(quizResponseAnswerDTO.getAnswer())
                .mark(quizResponseAnswerDTO.getMark())
                .question(quizResponseAnswerDTO.getQuestion());
        return quizResponseAnswerBuilder.build();
    }

    public static QuizResponseAnswerDTO toDto(QuizResponseAnswer quizResponseAnswer) {
        QuizResponseAnswerDTO.QuizResponseAnswerDTOBuilder quizResponseAnswerDTOBuilder = QuizResponseAnswerDTO.builder();
        quizResponseAnswerDTOBuilder.id(quizResponseAnswer.getId())
                .answer(quizResponseAnswer.getAnswer())
                .mark(quizResponseAnswer.getMark())
                .answer(quizResponseAnswer.getQuestion());
        return quizResponseAnswerDTOBuilder.build();
    }
}
