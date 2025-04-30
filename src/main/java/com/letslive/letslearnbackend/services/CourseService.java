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
                TopicDTO topicData = topicService.getTopicById(topicDTO.getId(), courseId, null);
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
                if (topic.getType().equals("assignment")) {
                    TopicAssignment topicAssignment = topicAssigmentRepository.findByTopicId(topic.getId()).orElseThrow(() -> new CustomException("Report to dev please", HttpStatus.INTERNAL_SERVER_ERROR));
                    LocalDateTime topicStart;
                    LocalDateTime topicEnd;
                    if (topicAssignment.getOpen() == null) topicStart = TimeUtils.MIN; else topicStart = TimeUtils.convertStringToLocalDateTime(topicAssignment.getOpen());
                    if (topicAssignment.getClose() == null) topicEnd = TimeUtils.MAX; else topicEnd = TimeUtils.convertStringToLocalDateTime(topicAssignment.getClose());
                    if (topicStart.isBefore(endTime) && topicEnd.isAfter(startTime)) {
                        singleAssignmentReportDTOs.add(topicService.getSingleAssignmentReport(courseId, topic.getId()));

                        // Count assignments ending this month
                        if (topicEnd.isAfter(monthStart) && topicEnd.isBefore(monthEnd) && topicEnd.isAfter(now)) {
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

        List<AllAssignmentsReportDTO.StudentInfoWithAverageMark> studentInfoWithAverageMarks = calculateAverageStudentScoreForAssignments(singleAssignmentReportDTOs);
        AllAssignmentsReportDTO reportDTO = new AllAssignmentsReportDTO();

        reportDTO.setAssignmentCount(singleAssignmentReportDTOs.size());
        reportDTO.setAvgMark(singleAssignmentReportDTOs.stream().mapToDouble(SingleAssignmentReportDTO::getAvgMark).average().orElse(0.0));
        reportDTO.setAvgCompletionRate(singleAssignmentReportDTOs.stream().mapToDouble(SingleAssignmentReportDTO::getCompletionRate).average().orElse(0.0));
        reportDTO.setNumberOfAssignmentEndsAtThisMonth(assignmentsEndingThisMonth[0]);
        reportDTO.setClosestNextEndAssignment(nextClosestEndTime[0]);
        reportDTO.setMarkDistributionCount(mergeMarkDistributionCount(singleAssignmentReportDTOs.stream().map(SingleAssignmentReportDTO::getMarkDistributionCount).toList()));
        reportDTO.setStudentInfoWithMarkAverage(studentInfoWithAverageMarks);

        reportDTO.setStudentWithMarkOver8(studentInfoWithAverageMarks.stream().filter(info -> info.getAverageMark() != null && info.getAverageMark() >= 8.0).toList());
        reportDTO.setStudentWithMarkOver5(studentInfoWithAverageMarks.stream().filter(info -> info.getAverageMark() != null && info.getAverageMark() >= 5.0 && info.getAverageMark() < 8.0).toList());
        reportDTO.setStudentWithMarkOver2(studentInfoWithAverageMarks.stream().filter(info -> info.getAverageMark() != null && info.getAverageMark() >= 2.0 && info.getAverageMark() < 5.0).toList());
        reportDTO.setStudentWithMarkOver0(studentInfoWithAverageMarks.stream().filter(info -> info.getAverageMark() != null && info.getAverageMark() < 2.0).toList());
        reportDTO.setStudentWithNoResponse(studentInfoWithAverageMarks.stream().filter(info -> !info.getSubmitted()).toList());

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
                    if (topicQuiz.getOpen() == null) topicStart = TimeUtils.MIN; else topicStart = TimeUtils.convertStringToLocalDateTime(topicQuiz.getOpen());
                    if (topicQuiz.getClose() == null) topicEnd = TimeUtils.MAX; else topicEnd = TimeUtils.convertStringToLocalDateTime(topicQuiz.getClose());
                    if (topicStart.isBefore(endTime) && topicEnd.isAfter(startTime)) {
                        singleQuizReportDTOs.add(topicService.getSingleQuizReport(courseId, topic.getId()));
                    }
                }
            });
        });

        List<SingleQuizReportDTO.StudentInfoAndMark> studentInfoAndMarks = calculateAverageStudentScoreForQuizzes(singleQuizReportDTOs);

        AllQuizzesReportDTO reportDTO = new AllQuizzesReportDTO();

        reportDTO.setQuizCount(singleQuizReportDTOs.size());
        reportDTO.setAvgCompletionPercentage(singleQuizReportDTOs.stream().mapToDouble(SingleQuizReportDTO::getCompletionRate).average().orElse(0));
        reportDTO.setMinQuestionCount(singleQuizReportDTOs.stream().mapToInt(rep -> (int) rep.getQuestionCount()).min().orElse(0));
        reportDTO.setMaxQuestionCount(singleQuizReportDTOs.stream().mapToInt(rep -> (int) rep.getQuestionCount()).max().orElse(0));
        reportDTO.setMinStudentScoreBase10(singleQuizReportDTOs.stream().mapToDouble(SingleQuizReportDTO::getMinStudentMarkBase10).min().orElse(0));
        reportDTO.setMaxStudentScoreBase10(singleQuizReportDTOs.stream().mapToDouble(SingleQuizReportDTO::getMaxStudentMarkBase10).max().orElse(0));
        reportDTO.setStudentInfoWithMarkAverage(studentInfoAndMarks);
        reportDTO.setStudentWithMarkOver8(studentInfoAndMarks.stream().filter(info -> info.getMark() != null && info.getMark() >= 8.0).toList());
        reportDTO.setStudentWithMarkOver5(studentInfoAndMarks.stream().filter(info -> info.getMark() != null && info.getMark() >= 5.0 && info.getMark() < 8.0).toList());
        reportDTO.setStudentWithMarkOver2(studentInfoAndMarks.stream().filter(info -> info.getMark() != null && info.getMark() >= 2.0 && info.getMark() < 5.0).toList());
        reportDTO.setStudentWithMarkOver0(studentInfoAndMarks.stream().filter(info -> info.getMark() != null && info.getMark() < 2.0).toList());
        reportDTO.setStudentWithNoResponse(studentInfoAndMarks.stream().filter(info -> !info.getSubmitted()).toList());
        reportDTO.setMarkDistributionCount(mergeMarkDistributionCount(singleQuizReportDTOs.stream().map(SingleQuizReportDTO::getMarkDistributionCount).toList()));
        reportDTO.setSingleQuizReports(singleQuizReportDTOs);
        reportDTO.setTrueFalseQuestionCount(singleQuizReportDTOs.stream().mapToInt(rep -> (int) rep.getTrueFalseQuestionCount()).sum());
        reportDTO.setMultipleChoiceQuestionCount(singleQuizReportDTOs.stream().mapToInt(rep -> (int) rep.getMultipleChoiceQuestionCount()).sum());
        reportDTO.setShortAnswerQuestionCount(singleQuizReportDTOs.stream().mapToInt(rep -> (int) rep.getShortAnswerQuestionCount()).sum());

        return reportDTO;
    }

    public List<SingleQuizReportDTO.StudentInfoAndMark> calculateAverageStudentScoreForQuizzes(List<SingleQuizReportDTO> singleQuizReports) {
        // Map to store student's scores across quizzes
        Map<UUID, List<Double>> studentScoresMap = new HashMap<>();
        // Map to store most recent StudentInfoAndMark for each student
        Map<UUID, SingleQuizReportDTO.StudentInfoAndMark> latestStudentInfo = new HashMap<>();

        // Collect scores and latest info for each student across all quizzes
        for (SingleQuizReportDTO report : singleQuizReports) {
            if (report.getStudentWithMark() == null) continue;

            report.getStudentWithMark().forEach(infoAndMark -> {
                if (infoAndMark.getStudent() != null && infoAndMark.getSubmitted() && report.getMaxDefaultMark() != null) {
                    UUID studentId = infoAndMark.getStudent().getId();
                    double percentage = (infoAndMark.getMark() / report.getMaxDefaultMark()) * 10;

                    // Store the score
                    studentScoresMap.computeIfAbsent(studentId, k -> new ArrayList<>()).add(percentage);

                    // Update or store the latest student info
                    latestStudentInfo.put(studentId, infoAndMark);
                }
            });
        }

        // Create final list with averaged scores but preserved info
        return studentScoresMap.entrySet().stream()
                .map(entry -> {
                    UUID studentId = entry.getKey();
                    List<Double> scores = entry.getValue();

                    // Get the existing student info
                    SingleQuizReportDTO.StudentInfoAndMark existingInfo = latestStudentInfo.get(studentId);

                    // Create new instance to avoid modifying the original
                    SingleQuizReportDTO.StudentInfoAndMark avgInfo = new SingleQuizReportDTO.StudentInfoAndMark();
                    // Copy all fields from existing info
                    avgInfo.setStudent(existingInfo.getStudent());
                    avgInfo.setSubmitted(existingInfo.getSubmitted());
                    avgInfo.setResponseId(existingInfo.getResponseId());

                    // Calculate and set the average mark
                    double averageMark = scores.stream()
                            .mapToDouble(Double::doubleValue)
                            .average()
                            .orElse(0.0);
                    avgInfo.setMark(averageMark);

                    return avgInfo;
                })
                .collect(Collectors.toList());
    }

    public List<AllAssignmentsReportDTO.StudentInfoWithAverageMark> calculateAverageStudentScoreForAssignments(List<SingleAssignmentReportDTO> singleAssignmentReports) {
        // Map to store student's scores across quizzes
        Map<UUID, List<Double>> studentScoresMap = new HashMap<>();
        // Map to store most recent StudentInfoAndMark for each student
        Map<UUID, SingleAssignmentReportDTO.StudentInfoAndMark> latestStudentInfo = new HashMap<>();

        // Collect scores and latest info for each student across all quizzes
        for (SingleAssignmentReportDTO report : singleAssignmentReports) {
            if (report.getStudentMarks() == null) continue;

            report.getStudentMarks().forEach(infoAndMark -> {
                if (infoAndMark.getStudent() != null && infoAndMark.getSubmitted()) {
                    UUID studentId = infoAndMark.getStudent().getId();
                    double percentage = infoAndMark.getMark();

                    // Store the score
                    studentScoresMap.computeIfAbsent(studentId, k -> new ArrayList<>()).add(percentage);

                    // Update or store the latest student info
                    latestStudentInfo.put(studentId, infoAndMark);
                }
            });
        }

        // Create final list with averaged scores but preserved info
        return studentScoresMap.entrySet().stream()
                .map(entry -> {
                    UUID studentId = entry.getKey();
                    List<Double> scores = entry.getValue();

                    // Get the existing student info
                    SingleAssignmentReportDTO.StudentInfoAndMark existingInfo = latestStudentInfo.get(studentId);

                    // Create new instance to avoid modifying the original
                    SingleAssignmentReportDTO.StudentInfoAndMark avgInfo = new SingleAssignmentReportDTO.StudentInfoAndMark();
                    // Copy all fields from existing info
                    avgInfo.setStudent(existingInfo.getStudent());
                    avgInfo.setSubmitted(existingInfo.getSubmitted());
                    avgInfo.setResponseId(existingInfo.getResponseId());

                    // Calculate and set the average mark
                    double averageMark = scores.stream()
                            .mapToDouble(Double::doubleValue)
                            .average()
                            .orElse(0.0);
                    avgInfo.setMark(averageMark);

                    return avgInfo;
                })
                .toList()
                .stream().map(set -> new AllAssignmentsReportDTO.StudentInfoWithAverageMark(set.getStudent(), set.getMark(), set.getSubmitted())).toList();
    }

    public Map<Number, Number> mergeMarkDistributionCount(List<Map<Number, Number>> markDistributionCount) {
        Map<Number, List<Number>> groupedValues = new HashMap<>();
        Number[] keys = {-1, 0, 2, 5, 8};

        for (Map<Number, Number> distribution : markDistributionCount) {
            for (Number key : keys) {
                Number value = distribution.getOrDefault(key, 0.0);
                groupedValues.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
            }
        }

        return groupedValues.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .mapToInt(Number::intValue)
                                .sum()
                ));
    }
}
