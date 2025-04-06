package com.letslive.letslearnbackend.services;

import com.letslive.letslearnbackend.dto.QuizResponseDTO;
import com.letslive.letslearnbackend.entities.QuizResponse;
import com.letslive.letslearnbackend.exception.CustomException;
import com.letslive.letslearnbackend.mappers.QuizResponseMapper;
import com.letslive.letslearnbackend.repositories.QuizResponseAnswerRepository;
import com.letslive.letslearnbackend.repositories.QuizResponseRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class QuizResponseService {
    private final QuizResponseRepository quizResponseRepository;
    private final QuizResponseAnswerRepository quizResponseAnswerRepository;

    public QuizResponseDTO getQuizResponseById(UUID id) {
        QuizResponse res = quizResponseRepository.findById(id).orElseThrow(() -> new CustomException("Quiz response not found", HttpStatus.NOT_FOUND));
        return QuizResponseMapper.toDto(res);
    }

    public QuizResponseDTO createQuizResponse(QuizResponseDTO quizResponseDTO) {
        QuizResponse quizResponse = QuizResponseMapper.toEntity(quizResponseDTO);
        QuizResponse savedQuizResponse = quizResponseRepository.save(quizResponse);
        quizResponse.getAnswers().forEach(answer -> {
            answer.setQuizResponseId(savedQuizResponse.getId());
            quizResponseAnswerRepository.save(answer);
        });
        savedQuizResponse.getAnswers().forEach(answer -> {answer.setQuizResponseId(savedQuizResponse.getId());});
        return QuizResponseMapper.toDto(savedQuizResponse);
    }

    public List<QuizResponseDTO> getAllQuizResponsesByTopicId(UUID topicId) {
        return quizResponseRepository.findAllByTopicId(topicId).stream().map(QuizResponseMapper::toDto).toList();
    }

    public QuizResponseDTO updateQuizResponseById(UUID id, QuizResponseDTO quizResponseDTO) {
        if (quizResponseRepository.existsById(id)) {
            throw new CustomException("Quiz response not found", HttpStatus.NOT_FOUND);
        }

        quizResponseDTO.setId(id);
        QuizResponse quizResponse = QuizResponseMapper.toEntity(quizResponseDTO);
        QuizResponse savedQuizResponse = quizResponseRepository.save(quizResponse);
        return QuizResponseMapper.toDto(savedQuizResponse);
    }
}
