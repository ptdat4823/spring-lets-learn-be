package com.letslive.letslearnbackend.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.letslive.letslearnbackend.dto.AllQuizzesReportDTO;
import com.letslive.letslearnbackend.dto.CourseDTO;
import com.letslive.letslearnbackend.dto.SingleQuizReportDTO;
import com.letslive.letslearnbackend.dto.TopicDTO;
import com.letslive.letslearnbackend.entities.AssignmentResponse;
import com.letslive.letslearnbackend.entities.Course;
import com.letslive.letslearnbackend.entities.QuizResponse;
import com.letslive.letslearnbackend.entities.User;
import com.letslive.letslearnbackend.exception.CustomException;
import com.letslive.letslearnbackend.mappers.CourseMapper;
import com.letslive.letslearnbackend.mappers.TopicMapper;
import com.letslive.letslearnbackend.repositories.*;
import com.letslive.letslearnbackend.utils.TimeUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final TopicQuizRepository topicQuizRepository;
    private final ObjectMapper mapper = new ObjectMapper();
    private final TopicAssigmentRepository topicAssigmentRepository;
    private final TopicMeetingRepository topicMeetingRepository;
    private final QuizResponseRepository quizResponseRepository;
    private final AssignmentResponseRepository assignmentResponseRepository;
    private final TopicService topicService;

    // get course DOES NOT have the topic data with it, must populate it manually
    private CourseDTO getCourseWithTopicData(UUID courseId) {
        Course course = courseRepository
                .findById(courseId)
                .orElseThrow(() -> new CustomException("Course not found", HttpStatus.NOT_FOUND));

        CourseDTO courseDTO = CourseMapper.mapToDTO(course);

        courseDTO.getSections().forEach(sectionDTO -> {
            sectionDTO.getTopics().forEach(topicDTO -> {
                TopicDTO topicData = topicService.getTopicById(topicDTO.getId(), null);
                topicDTO.setData(topicData.getData());
            });
        });

        return courseDTO;
    }

    public List<CourseDTO> getAllCoursesByUserID(UUID userID) {
        userRepository
                .findById(userID)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        return courseRepository
                .findByCreatorId(userID)
                .stream()
                .map(c -> getCourseWithTopicData(c.getId()))
                .toList();
    }

    public CourseDTO getCourse(UUID id) {
        return getCourseWithTopicData(id);
    }

    public List<CourseDTO> getAllPublicCourses() {
        List<Course> courses = courseRepository.findAllByIsPublishedTrue();
        return courses.stream().map(CourseMapper::mapToDTO).toList();
    }

    public CourseDTO createCourse(User creator, CourseDTO courseDTO) {
        Course course = CourseMapper.mapToEntity(courseDTO);
        course.setCreator(creator);

        Course createdCourse = courseRepository.save(course);
        return CourseMapper.mapToDTO(createdCourse);
    }

    public CourseDTO updateCourse(UUID id, CourseDTO courseDTO) {
        Course course = courseRepository.findById(id).orElseThrow(() -> new CustomException("Course not found", HttpStatus.NOT_FOUND));

        course.setTitle(courseDTO.getTitle());
        course.setDescription(courseDTO.getDescription());
        course.setImageUrl(courseDTO.getImageUrl());
        course.setCategory(courseDTO.getCategory());
        course.setLevel(courseDTO.getLevel());
        course.setIsPublished(courseDTO.getIsPublished());

        Course updatedCourse = courseRepository.save(course);
        return CourseMapper.mapToDTO(updatedCourse);
    }

    public void addUserToCourse(UUID id, UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
        Course course = courseRepository.findById(id).orElseThrow(() -> new CustomException("Course not found", HttpStatus.NOT_FOUND));

        if (course.getStudents().stream().anyMatch((student) -> student.getId().equals(userId))) {
            throw new CustomException("User already has this course", HttpStatus.CONFLICT);
        };

        user.getCourses().add(course);
        course.getStudents().add(user);
        course.setTotalJoined(course.getTotalJoined() + 1);

        courseRepository.save(course);
        userRepository.save(user);
    }

    public List<TopicDTO> getAllWorksOfCourseAndUser(UUID courseId, UUID userId, String type, LocalDateTime start, LocalDateTime end) {
        if ((start != null || end != null) && (start == null || end == null)) throw new CustomException("Provide start and end time!", HttpStatus.BAD_REQUEST);
        if (start != null && start.isAfter(end)) throw new CustomException("Start time must be after end time", HttpStatus.BAD_REQUEST);

        Course course = courseRepository
                .findById(courseId)
                .orElseThrow(() -> new CustomException("Course not found", HttpStatus.NOT_FOUND));

        List<TopicDTO> result = new ArrayList<>();

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

                                        Optional<QuizResponse> res = quizResponseRepository.findByTopicIdAndStudentId(topicQuiz.getTopicId(), userId);
                                        if (res.isPresent()) {
                                            String resData = mapper.writeValueAsString(res.get());
                                            topicDTO.setResponse(resData);
                                        }

                                        topicDTO.setData(data);
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

        return result;
    }

    public AllQuizzesReportDTO getQuizzesReport(UUID courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new CustomException("Course not found", HttpStatus.NOT_FOUND));
        List<SingleQuizReportDTO> singleQuizReportDTOS = new ArrayList<>();

        course.getSections().forEach(courseSection -> {
            courseSection.getTopics().forEach(topicSection -> {
                if (topicSection.getType().equals("quiz")) {
                    singleQuizReportDTOS.add(topicService.getSingleQuizReport(courseId, topicSection.getId()));
                }
            });
        });

        return calculateAllQuizzesReport(singleQuizReportDTOS);
    }

    public AllQuizzesReportDTO calculateAllQuizzesReport(List<SingleQuizReportDTO> singleQuizReports) {
        // Quiz count
        int quizCount = singleQuizReports.size();

        // Average completion percentage
        double completionPercentage = singleQuizReports.stream()
                .mapToDouble(SingleQuizReportDTO::getCompletionRate)
                .average()
                .orElse(0.0);

        // Average mark
        double avgMark = singleQuizReports.stream()
                .mapToDouble(SingleQuizReportDTO::getAvgMark)
                .average()
                .orElse(0.0);

        // Aggregate question types by percentage
        Map<String, Double> questionTypeByPercentage = singleQuizReports.stream()
                .flatMap(quiz -> quiz.getQuestionTypeByPercentage().entrySet().stream())
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey, // Group by question type
                        Collectors.averagingDouble(Map.Entry::getValue) // Average percentage across quizzes
                ));

        // Aggregate scores by percentage
        Map<Double, Double> aggregatedScores = singleQuizReports.stream()
                .flatMap(quiz -> quiz.getScoresByPercentage().stream())
                .collect(Collectors.groupingBy(
                        SingleQuizReportDTO.ScoreByPercentage::getScore,
                        Collectors.averagingDouble(SingleQuizReportDTO.ScoreByPercentage::getPercentage)
                ));

        // Assemble the AllQuizzesReportDTO
        AllQuizzesReportDTO allQuizzesReport = new AllQuizzesReportDTO();
        allQuizzesReport.setQuizCount(quizCount);
        allQuizzesReport.setCompletionPercentage(completionPercentage);
        allQuizzesReport.setAvgMark(avgMark);
        allQuizzesReport.setQuestionTypeByPercentage(questionTypeByPercentage);
        allQuizzesReport.setScoreByPercentage(mergeScoresByPercentage(singleQuizReports));
        allQuizzesReport.setSingleQuizReports(singleQuizReports);

        return allQuizzesReport;
    }

    public List<SingleQuizReportDTO.ScoreByPercentage> mergeScoresByPercentage(List<SingleQuizReportDTO> singleQuizReports) {
        // Step 1: Aggregate scores with weights
        Map<Double, double[]> scoreAggregates = singleQuizReports.stream()
                .flatMap(quiz -> quiz.getScoresByPercentage().stream()
                        .map(scoreByPercentage -> Map.entry(
                                scoreByPercentage.getScore(), // Group by score (Double)
                                new double[]{scoreByPercentage.getPercentage(), quiz.getStudentCount().doubleValue()} // Percentage and weight
                        ))
                )
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.reducing(
                                new double[]{0.0, 0.0}, // Initial value: {percentageSum, weightSum}
                                Map.Entry::getValue,
                                (a, b) -> new double[]{a[0] + b[0] * b[1], a[1] + b[1]} // Combine percentages weighted by student count
                        )
                ));

        // Step 2: Calculate final percentage and convert to ScoreByPercentage list
        return scoreAggregates.entrySet().stream()
                .map(entry -> new SingleQuizReportDTO.ScoreByPercentage(
                        entry.getKey(), // Score
                        entry.getValue()[1] == 0.0 ? 0.0 : (entry.getValue()[0] / entry.getValue()[1]) // Weighted average
                ))
                .collect(Collectors.toList());
    }

}
