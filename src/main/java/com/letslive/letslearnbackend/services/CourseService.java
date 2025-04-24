package com.letslive.letslearnbackend.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.letslive.letslearnbackend.dto.*;
import com.letslive.letslearnbackend.entities.*;
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
import java.util.concurrent.atomic.AtomicInteger;
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
    private final EnrollmentDetailRepository enrollmentDetailRepository;

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

        if (enrollmentDetailRepository.existsByStudentIdAndCourseId(userId, course.getId())) {
            throw new CustomException("User is already enrolled to this course", HttpStatus.BAD_REQUEST);
        }

        EnrollmentDetail enrollmentDetail = new EnrollmentDetail();
        enrollmentDetail.setCourse(course);
        enrollmentDetail.setStudent(user);
        course.setTotalJoined(course.getTotalJoined() == null ? 1 : course.getTotalJoined() + 1);

        courseRepository.save(course);
        enrollmentDetailRepository.save(enrollmentDetail);
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

                                        List<QuizResponse> res = quizResponseRepository.findByTopicIdAndStudentId(topicQuiz.getTopicId(), userId);
                                        String resData = mapper.writeValueAsString(res);
                                        topicDTO.setResponse(resData);

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

    public AllAssignmentsReportDTO getAssignmentsReport(UUID courseId, LocalDateTime startTime, LocalDateTime endTime) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new CustomException("Course not found", HttpStatus.NOT_FOUND));
        List<SingleAssignmentReportDTO> singleAssignmentReportDTOs = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime monthStart = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime monthEnd = now.withDayOfMonth(now.toLocalDate().lengthOfMonth())
                .withHour(23).withMinute(59).withSecond(59);
        final AtomicInteger[] assignmentsEndingThisMonth = {new AtomicInteger()};
        final LocalDateTime[] nextClosestEndTime = {null};

        course.getSections().forEach(courseSection -> {
            courseSection.getTopics().forEach(topic -> {
                if (topic.getType().equals("quiz")) {
                    TopicAssignment topicAssignment = topicAssigmentRepository.findByTopicId(topic.getId()).orElseThrow(() -> new CustomException("Report to dev please", HttpStatus.INTERNAL_SERVER_ERROR));
                    LocalDateTime topicStart;
                    LocalDateTime topicEnd;
                    if (topicAssignment.getOpen() == null) topicStart = LocalDateTime.MIN; else topicStart = TimeUtils.convertStringToLocalDateTime(topicAssignment.getOpen());
                    if (topicAssignment.getClose() == null) topicEnd = LocalDateTime.MAX; else topicEnd = TimeUtils.convertStringToLocalDateTime(topicAssignment.getClose());
                    if (topicStart.isBefore(endTime) && topicEnd.isAfter(startTime)) {
                        singleAssignmentReportDTOs.add(topicService.getSingleAssignmentReport(courseId, topic.getId()));

                        // Count assignments ending this month
                        if (topicEnd.isAfter(monthStart) && topicEnd.isBefore(monthEnd)) {
                            assignmentsEndingThisMonth[0].getAndIncrement();
                        }

                        // Find next closest end time
                        if (topicEnd.isAfter(now)) {
                            if (nextClosestEndTime[0] == null || topicEnd.isBefore(nextClosestEndTime[0])) {
                                nextClosestEndTime[0] = topicEnd;
                            }
                        }
                    }
                }
            });
        });

        AllAssignmentsReportDTO reportDTO = new AllAssignmentsReportDTO();

        reportDTO.setAssignmentCount(singleAssignmentReportDTOs.size());
        reportDTO.setAvgMark(singleAssignmentReportDTOs.stream().mapToDouble(SingleAssignmentReportDTO::getAvgMark).average().orElse(0.0));
        reportDTO.setAvgCompletionRate(singleAssignmentReportDTOs.stream().mapToDouble(SingleAssignmentReportDTO::getCompletionRate).average().orElse(0.0));
        reportDTO.setNumberOfAssignmentEndsAtThisMonth(assignmentsEndingThisMonth[0]);
        reportDTO.setClosestNextEndAssignment(nextClosestEndTime[0]);
        reportDTO.setMarkDistributionByPercentage(calculateAverageMarkDistributions(singleAssignmentReportDTOs.stream().map(SingleAssignmentReportDTO::getMarkDistributionByPercentage).toList()));
        reportDTO.setStudentMarks(calculateAverageStudentScorePercentageForAssignments(singleAssignmentReportDTOs));
        reportDTO.setFileTypeCount(singleAssignmentReportDTOs.stream().map(SingleAssignmentReportDTO::getFileTypeCount)
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.summingLong(Map.Entry::getValue)
                )));
        reportDTO.setSingleAssignmentReports(singleAssignmentReportDTOs);

        return reportDTO;
    }

    public AllQuizzesReportDTO getQuizzesReport(UUID courseId, LocalDateTime startTime, LocalDateTime endTime) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new CustomException("Course not found", HttpStatus.NOT_FOUND));
        List<SingleQuizReportDTO> singleQuizReportDTOs = new ArrayList<>();

        course.getSections().forEach(courseSection -> {
            courseSection.getTopics().forEach(topic -> {
                if (topic.getType().equals("quiz")) {
                    TopicQuiz topicQuiz = topicQuizRepository.findByTopicId(topic.getId()).orElseThrow(() -> new CustomException("Report to dev please", HttpStatus.INTERNAL_SERVER_ERROR));
                    LocalDateTime topicStart;
                    LocalDateTime topicEnd;
                    if (topicQuiz.getOpen() == null) topicStart = LocalDateTime.MIN; else topicStart = TimeUtils.convertStringToLocalDateTime(topicQuiz.getOpen());
                    if (topicQuiz.getClose() == null) topicEnd = LocalDateTime.MAX; else topicEnd = TimeUtils.convertStringToLocalDateTime(topicQuiz.getClose());
                    if (topicStart.isBefore(endTime) && topicEnd.isAfter(startTime)) {
                        singleQuizReportDTOs.add(topicService.getSingleQuizReport(courseId, topic.getId()));
                    }
                }
            });
        });

        AllQuizzesReportDTO reportDTO = new AllQuizzesReportDTO();

        reportDTO.setQuizCount(singleQuizReportDTOs.size());
        reportDTO.setAvgCompletionPercentage(singleQuizReportDTOs.stream().mapToDouble(SingleQuizReportDTO::getCompletionRate).average().orElse(0));
        reportDTO.setMinQuestionCount(singleQuizReportDTOs.stream().mapToInt(rep -> (int) rep.getQuestionCount()).min().orElse(0));
        reportDTO.setMaxQuestionCount(singleQuizReportDTOs.stream().mapToInt(rep -> (int) rep.getQuestionCount()).max().orElse(0));
        reportDTO.setMinStudentScoreBase10(singleQuizReportDTOs.stream().mapToDouble(SingleQuizReportDTO::getMinStudentMarkBase10).min().orElse(0));
        reportDTO.setMaxStudentScoreBase10(singleQuizReportDTOs.stream().mapToDouble(SingleQuizReportDTO::getMaxStudentMarkBase10).max().orElse(0));
        reportDTO.setStudentMarkPercentages(calculateAverageStudentScorePercentageForQuizzes(singleQuizReportDTOs));
        reportDTO.setMarkDistributionByPercentage(calculateAverageMarkDistributions(singleQuizReportDTOs.stream().map(SingleQuizReportDTO::getMarkDistributionByPercentage).toList()));
        reportDTO.setSingleQuizReports(singleQuizReportDTOs);
        reportDTO.setTrueFalseQuestionCount(singleQuizReportDTOs.stream().mapToInt(rep -> (int) rep.getTrueFalseQuestionCount()).sum());
        reportDTO.setMultipleChoiceQuestionCount(singleQuizReportDTOs.stream().mapToInt(rep -> (int) rep.getMultipleChoiceQuestionCount()).sum());
        reportDTO.setShortAnswerQuestionCount(singleQuizReportDTOs.stream().mapToInt(rep -> (int) rep.getShortAnswerQuestionCount()).sum());

        return reportDTO;
    }

    public Map<UUID, Double> calculateAverageStudentScorePercentageForQuizzes(List<SingleQuizReportDTO> singleQuizReports) {
        Map<UUID, List<Double>> studentScorePercentages = new HashMap<>();

        // Calculate percentage scores for each quiz and collect them by student
        for (SingleQuizReportDTO report : singleQuizReports) {
            report.getStudentWithMark().forEach((studentId, mark) -> {
                double percentage = (mark / report.getMaxDefaultMark()) * 100;
                studentScorePercentages.computeIfAbsent(studentId, k -> new ArrayList<>()).add(percentage);
            });
        }

        // Calculate average percentage for each student
        return studentScorePercentages.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .mapToDouble(Double::doubleValue)
                                .average()
                                .orElse(0.0)
                ));
    }

    public Map<UUID, Double> calculateAverageStudentScorePercentageForAssignments(List<SingleAssignmentReportDTO> singleAssignmentReports) {
        Map<UUID, List<Double>> studentScorePercentages = new HashMap<>();

        // Calculate percentage scores for each quiz and collect them by student
        for (SingleAssignmentReportDTO report : singleAssignmentReports) {
            report.getStudentMarks().forEach((studentId, mark) -> {
                studentScorePercentages.computeIfAbsent(studentId, k -> new ArrayList<>()).add(mark);
            });
        }

        // Calculate average percentage for each student
        return studentScorePercentages.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .mapToDouble(Double::doubleValue)
                                .average()
                                .orElse(0.0)
                ));
    }

    public Map<Number, Double> calculateAverageMarkDistributions(List<Map<Number, Double>> markDistributions) {
        Map<Number, List<Double>> groupedValues = new HashMap<>();
        Number[] keys = {-1, 0, 2, 5, 8};

        for (Map<Number, Double> distribution : markDistributions) {
            for (Number key : keys) {
                Double value = distribution.getOrDefault(key, 0.0);
                groupedValues.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
            }
        }

        return groupedValues.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .mapToDouble(Double::doubleValue)
                                .average()
                                .orElse(0.0)
                ));
    }
}
