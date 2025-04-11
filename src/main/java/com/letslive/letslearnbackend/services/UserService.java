package com.letslive.letslearnbackend.services;

import com.letslive.letslearnbackend.dto.AssignmentResponseDTO;
import com.letslive.letslearnbackend.dto.QuizResponseDTO;
import com.letslive.letslearnbackend.dto.UserDTO;
import com.letslive.letslearnbackend.entities.QuizResponse;
import com.letslive.letslearnbackend.entities.User;
import com.letslive.letslearnbackend.exception.CustomException;
import com.letslive.letslearnbackend.mappers.AssignmentResponseMapper;
import com.letslive.letslearnbackend.mappers.QuizResponseMapper;
import com.letslive.letslearnbackend.mappers.UserMapper;
import com.letslive.letslearnbackend.repositories.AssignmentResponseRepository;
import com.letslive.letslearnbackend.repositories.CourseRepository;
import com.letslive.letslearnbackend.repositories.QuizResponseRepository;
import com.letslive.letslearnbackend.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final QuizResponseRepository quizResponseRepository;
    private final AssignmentResponseRepository assignmentResponseRepository;

    public UserDTO findUserById(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        assignmentResponseRepository.findAllByStudentId(user.getId()).stream().map(AssignmentResponseMapper::toDTO).toList();
        assignmentResponseRepository.findAllByStudentId(user.getId()).stream().map(AssignmentResponseMapper::toDTO).toList();

        return UserMapper.mapToDTO(user);
    }

    public List<QuizResponseDTO> getAllQuizResponsesOfUser(UUID userId) {
        if (!userRepository.existsById(userId)) throw new CustomException("User not found", HttpStatus.NOT_FOUND);
        return quizResponseRepository.findAllByStudentId(userId).stream().map(QuizResponseMapper::toDto).toList();
    }

    public List<AssignmentResponseDTO> getAllAssignmentResponsesOfUser(UUID userId) {
        if (!userRepository.existsById(userId)) throw new CustomException("User not found", HttpStatus.NOT_FOUND);
        return assignmentResponseRepository.findAllByStudentId(userId).stream().map(AssignmentResponseMapper::toDTO).toList();
    }
}