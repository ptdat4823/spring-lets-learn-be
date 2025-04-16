package com.letslive.letslearnbackend.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.letslive.letslearnbackend.dto.*;
import com.letslive.letslearnbackend.entities.User;
import com.letslive.letslearnbackend.exception.CustomException;
import com.letslive.letslearnbackend.mappers.*;
import com.letslive.letslearnbackend.repositories.*;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final QuizResponseRepository quizResponseRepository;
    private final AssignmentResponseRepository assignmentResponseRepository;
    private final TopicQuizRepository topicQuizRepository;
    private final ObjectMapper mapper = new ObjectMapper();
    private final TopicAssigmentRepository topicAssigmentRepository;
    private final TopicMeetingRepository topicMeetingRepository;

    public UserDTO findUserById(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
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

    public List<CourseQuizzesDTO> getAllQuizzesOfUser(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
        List<CourseQuizzesDTO> result = new ArrayList<>();

        user.getCourses().forEach(course -> {
            List<String> quizzes = new ArrayList<>();

            course.getSections().forEach(courseSection -> {
                courseSection.getTopics().forEach(topicSection -> {
                    if (Objects.equals(topicSection.getType(), "quiz")) {
                        topicQuizRepository.findByTopicId(topicSection.getId()).ifPresent(topicQuiz -> {
                            try {
                                quizzes.add(mapper.writeValueAsString(topicQuiz));
                            } catch (JsonProcessingException e) {
                                throw new CustomException("Something went wrong!", HttpStatus.INTERNAL_SERVER_ERROR);
                            }
                        });
                    }
                });
            });

            result.add(new CourseQuizzesDTO(
                    CourseMapper.mapToDTO(course),
                    quizzes
            ));
        });

        return result;
    }

    public List<StudentWorksInACourseDTO> getAllWorksOfUser(UUID userId, String type, UUID courseId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
        List<StudentWorksInACourseDTO> result = new ArrayList<>();

        user.getCourses().forEach(course -> {
            List<TopicDTO> works = new ArrayList<>();

            course.getSections().forEach(courseSection -> {
                courseSection.getTopics().forEach(topicSection -> {
                    if (type == null || type.isEmpty() || type.equals(topicSection.getType())) {
                        switch (topicSection.getType()) {
                            case "quiz":
                                if (type == null || type.equals("quiz")) {
                                    topicQuizRepository.findByTopicId(topicSection.getId()).ifPresent(topicQuiz -> {
                                        try {
                                            String data = mapper.writeValueAsString(topicQuiz);
                                            TopicDTO topicDTO = TopicMapper.toDTO(topicSection);
                                            topicDTO.setData(data);
                                            works.add(topicDTO);
                                        } catch (JsonProcessingException e) {
                                            throw new CustomException("Something went wrong!", HttpStatus.INTERNAL_SERVER_ERROR);
                                        }
                                    });
                                }
                                break;
                            case "assignment":
                                if (type == null || type.equals("assignment")) {
                                    topicAssigmentRepository.findByTopicId(topicSection.getId()).ifPresent(topicAssignment -> {
                                        try {
                                            String data = mapper.writeValueAsString(topicAssignment);
                                            TopicDTO topicDTO = TopicMapper.toDTO(topicSection);
                                            topicDTO.setData(data);
                                            works.add(topicDTO);
                                        } catch (JsonProcessingException e) {
                                            throw new CustomException("Something went wrong!", HttpStatus.INTERNAL_SERVER_ERROR);
                                        }
                                    });
                                }
                                break;
                            case "meeting":
                                if (type == null || type.equals("meeting")) {
                                    topicMeetingRepository.findByTopicId(topicSection.getId()).ifPresent(topicMeeting -> {
                                        try {
                                            String data = mapper.writeValueAsString(topicMeeting);
                                            TopicDTO topicDTO = TopicMapper.toDTO(topicSection);
                                            topicDTO.setData(data);
                                            works.add(topicDTO);
                                        } catch (JsonProcessingException e) {
                                            throw new CustomException("Something went wrong!", HttpStatus.INTERNAL_SERVER_ERROR);
                                        }
                                    });
                                }
                                break;
                            default:
                                throw new CustomException("Type not found, something went wrong!", HttpStatus.INTERNAL_SERVER_ERROR);
                        }
                    }
                });
            });

            if (!works.isEmpty()) {
                result.add(new StudentWorksInACourseDTO(
                        CourseMapper.mapToDTO(course),
                        works
                ));
            }
        });

        return result;
    }
}