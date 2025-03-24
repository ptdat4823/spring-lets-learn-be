package com.letslive.letslearnbackend.services;

import com.letslive.letslearnbackend.dto.QuestionDTO;
import com.letslive.letslearnbackend.entities.Question;
import com.letslive.letslearnbackend.exception.CustomException;
import com.letslive.letslearnbackend.mappers.QuestionMapper;
import com.letslive.letslearnbackend.repositories.QuestionRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;

    public QuestionDTO createQuestion(QuestionDTO questionDTO) {
        Question createdQuestion = questionRepository.save(QuestionMapper.toEntity(questionDTO));
        return QuestionMapper.toDTO(createdQuestion);
    }

    public List<QuestionDTO> getAllQuestionsByCourseId(UUID courseId) {
        return questionRepository.findAllByCourseId(courseId).stream().map((question -> QuestionMapper.toDTO(question))).toList();
    }

    public QuestionDTO getQuestionById(UUID questionId) {
        return QuestionMapper.toDTO(questionRepository.findById(questionId).orElseThrow(() -> new CustomException("No question found", HttpStatus.NOT_FOUND)));
    }

    public QuestionDTO updateQuestion(UUID questionId, QuestionDTO questionDTO) {
        questionRepository.findById(questionId).orElseThrow(() -> new CustomException("No question found", HttpStatus.NOT_FOUND));

        Question updatedQuestion = QuestionMapper.toEntity(questionDTO);
        return QuestionMapper.toDTO(questionRepository.save(updatedQuestion));
    }
}
