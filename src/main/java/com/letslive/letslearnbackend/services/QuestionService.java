package com.letslive.letslearnbackend.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.letslive.letslearnbackend.dto.QuestionDTO;
import com.letslive.letslearnbackend.entities.Question;
import com.letslive.letslearnbackend.entities.QuestionChoice;
import com.letslive.letslearnbackend.exception.CustomException;
import com.letslive.letslearnbackend.mappers.QuestionChoiceMapper;
import com.letslive.letslearnbackend.mappers.QuestionMapper;
import com.letslive.letslearnbackend.repositories.QuestionChoiceRepository;
import com.letslive.letslearnbackend.repositories.QuestionRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final QuestionChoiceRepository questionChoiceRepository;

    public QuestionDTO createQuestion(QuestionDTO questionDTO) {
        // create question
        Question firstSavedQuestion = questionRepository.save(QuestionMapper.toEntity(questionDTO));

        // create question choices
        if (questionDTO.getChoices() != null && !questionDTO.getChoices().isEmpty()) {
            questionDTO.getChoices().stream().forEach((choice) -> {
                choice.setQuestionId(firstSavedQuestion.getId());
                QuestionChoice createdQuestionChoice = questionChoiceRepository.save(QuestionChoiceMapper.toEntity(choice));
                choice.setId(createdQuestionChoice.getId());
            });
        }

        // get the final question
        Question createdQuestion = questionRepository.findById(firstSavedQuestion.getId()).orElseThrow(() -> new CustomException("Something unexpected happened", HttpStatus.INTERNAL_SERVER_ERROR));

        return QuestionMapper.toDTO(createdQuestion);
    }

    public List<QuestionDTO> getAllQuestionsByCourseId(UUID courseId) {
        return questionRepository.findAllByCourseId(courseId).stream().map((question -> QuestionMapper.toDTO(question))).toList();
    }

    public QuestionDTO getQuestionById(UUID questionId) {
        return QuestionMapper.toDTO(questionRepository.findById(questionId).orElseThrow(() -> new CustomException("No question found", HttpStatus.NOT_FOUND)));
    }

    @Transactional
    public QuestionDTO updateQuestion(UUID questionId, QuestionDTO questionDTO) {
        questionRepository.findById(questionId).orElseThrow(() -> new CustomException("No question found", HttpStatus.NOT_FOUND));
        // check if exists
        if (questionDTO.getChoices() != null && !questionDTO.getChoices().isEmpty()) {
            questionDTO.getChoices().stream().forEach((choice) -> questionChoiceRepository.findById(choice.getId()).orElseThrow(() -> new CustomException("No question found", HttpStatus.NOT_FOUND)));
        }

        questionRepository.findById(questionId).orElseThrow(() -> new CustomException("No question found", HttpStatus.NOT_FOUND));

        // update choices first then questions
        if (questionDTO.getChoices() != null && !questionDTO.getChoices().isEmpty()) {
            questionDTO.getChoices().stream().forEach((choice) -> questionChoiceRepository.save(QuestionChoiceMapper.toEntity(choice)));
        }

        Question updateQuestion = QuestionMapper.toEntity(questionDTO);
        questionRepository.save(updateQuestion);

        Question createdQuestion = questionRepository.findById(questionId).orElseThrow(() -> new CustomException("Something unexpected happened", HttpStatus.INTERNAL_SERVER_ERROR));

        return QuestionMapper.toDTO(createdQuestion);
    }
}
