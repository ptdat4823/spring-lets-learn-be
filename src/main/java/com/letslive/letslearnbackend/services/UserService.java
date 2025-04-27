package com.letslive.letslearnbackend.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.letslive.letslearnbackend.dto.*;
import com.letslive.letslearnbackend.entities.AssignmentResponse;
import com.letslive.letslearnbackend.entities.EnrollmentDetail;
import com.letslive.letslearnbackend.entities.QuizResponse;
import com.letslive.letslearnbackend.entities.User;
import com.letslive.letslearnbackend.exception.CustomException;
import com.letslive.letslearnbackend.mappers.*;
import com.letslive.letslearnbackend.repositories.*;
import com.letslive.letslearnbackend.utils.TimeUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    private final TopicRepository topicRepository;
    private final EnrollmentDetailRepository enrollmentDetailRepository;

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

    public List<TopicDTO> getAllWorksOfUser(UUID userId, String type, LocalDateTime start, LocalDateTime end) {
        if ((start != null || end != null) && (start == null || end == null)) throw new CustomException("Provide start and end time!", HttpStatus.BAD_REQUEST);
        if (start != null && start.isAfter(end)) throw new CustomException("Start time must be after end time", HttpStatus.BAD_REQUEST);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
        List<TopicDTO> result = new ArrayList<>();

        user.getEnrollmentDetails().stream().map(EnrollmentDetail::getCourse).forEach(course -> {
            course.getSections().forEach(courseSection -> {
                courseSection.getTopics().forEach(topicSection -> {
                    if (type == null || type.isEmpty() || type.equals(topicSection.getType())) {
                        switch (topicSection.getType()) {
                            case "quiz":
                                if (type == null || type.equals("quiz")) {
                                    topicQuizRepository.findByTopicId(topicSection.getId()).ifPresent(topicQuiz -> {
                                        if (end != null) {
                                            LocalDateTime endTime = TimeUtils.convertStringToLocalDateTime(topicQuiz.getClose());
                                            if (!endTime.isBefore(end)) return;
                                        }

                                        try {
                                            String data = mapper.writeValueAsString(topicQuiz);
                                            TopicDTO topicDTO = TopicMapper.toDTO(topicSection);

                                            List<QuizResponse> res = quizResponseRepository.findByTopicIdAndStudentId(topicQuiz.getTopicId(), userId);
                                            String resData = mapper.writeValueAsString(res);
                                            topicDTO.setResponse(resData);

                                            topicDTO.setData(data);
                                            topicDTO.setCourse(CourseMapper.mapToDTO(course));
                                            result.add(topicDTO);
                                        } catch (JsonProcessingException e) {
                                            throw new CustomException("Something went wrong!", HttpStatus.INTERNAL_SERVER_ERROR);
                                        }
                                    });
                                }
                                break;
                            case "assignment":
                                if (type == null || type.equals("assignment")) {
                                    topicAssigmentRepository.findByTopicId(topicSection.getId()).ifPresent(topicAssignment -> {
                                        if (end != null) {
                                            LocalDateTime endTime = TimeUtils.convertStringToLocalDateTime(topicAssignment.getClose());
                                            if (!endTime.isBefore(end)) return;
                                        }

                                        try {
                                            String data = mapper.writeValueAsString(topicAssignment);
                                            TopicDTO topicDTO = TopicMapper.toDTO(topicSection);

                                            Optional<AssignmentResponse> res = assignmentResponseRepository.findByTopicIdAndStudentId(topicAssignment.getTopicId(), userId);
                                            if (res.isPresent()) {
                                                String resData = mapper.writeValueAsString(res.get());
                                                topicDTO.setResponse(resData);
                                            }

                                            topicDTO.setData(data);
                                            topicDTO.setCourse(CourseMapper.mapToDTO(course));
                                            result.add(topicDTO);
                                        } catch (JsonProcessingException e) {
                                            throw new CustomException("Something went wrong!", HttpStatus.INTERNAL_SERVER_ERROR);
                                        }
                                    });
                                }
                                break;
                            case "meeting":
                                if (type == null || type.equals("meeting")) {
                                    topicMeetingRepository.findByTopicId(topicSection.getId()).ifPresent(topicMeeting -> {
                                        if (end != null) {
                                            LocalDateTime startTime = TimeUtils.convertStringToLocalDateTime(topicMeeting.getOpen());
                                            if (!(startTime.isAfter(start) && startTime.isBefore(end))) return;
                                        }

                                        try {
                                            String data = mapper.writeValueAsString(topicMeeting);
                                            TopicDTO topicDTO = TopicMapper.toDTO(topicSection);
                                            topicDTO.setData(data);
                                            topicDTO.setCourse(CourseMapper.mapToDTO(course));
                                            result.add(topicDTO);
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
        });

        return result;
    }

    public UserDTO updateUserById(UpdateUserDTO body, UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
        user.setUsername(body.getUsername());
        user.setAvatar(body.getAvatar());
        userRepository.save(user);
        return UserMapper.mapToDTO(user);
    }

    public StudentReportDTO getStudentReport(UUID userId, LocalDateTime start, LocalDateTime end) {
        List<EnrollmentDetail> enrollmentDetails = enrollmentDetailRepository.findByStudentIdAndJoinDateLessThanEqual(userId, end);
//        Topic topic = topicRepository.findById(topicId).orElseThrow(() -> new CustomException("No topic found!", HttpStatus.NOT_FOUND));
        SingleQuizReportDTO reportDTO = new SingleQuizReportDTO();

        StudentReportDTO report = new StudentReportDTO();

//        report.setDaysWithAvgQuizMark();
//        report.setDaysWithAvgAssignmentMark();
//        report.setTotalQuizCount();
//        report.setTotalAssignmentCount();
//        report.setQuizToDo();
//        report.setAssignmentToDo();
//        report.setAvgQuizMark();
//        report.setAvgAssignmentMark();
//        report.setTopTopicQuizWithMark();
//        report.setTopTopicAssignmentWithMark();

        return null;
    }
}