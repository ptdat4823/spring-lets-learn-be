package com.letslive.letslearnbackend.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.letslive.letslearnbackend.dto.QuizResponseDTO;
import com.letslive.letslearnbackend.dto.SingleQuizReportDTO;
import com.letslive.letslearnbackend.dto.TopicDTO;
import com.letslive.letslearnbackend.entities.*;
import com.letslive.letslearnbackend.exception.CustomException;
import com.letslive.letslearnbackend.mappers.TopicMapper;
import com.letslive.letslearnbackend.mappers.UserMapper;
import com.letslive.letslearnbackend.repositories.*;
import com.letslive.letslearnbackend.utils.TimeUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TopicService {
    private final TopicRepository topicRepository;
    private final TopicQuizRepository topicQuizRepository;
    private final TopicQuizQuestionRepository topicQuizQuestionRepository;
    private final TopicQuizQuestionChoiceRepository topicQuizQuestionChoiceRepository;
    private final TopicAssigmentRepository topicAssigmentRepository;
    private final TopicMeetingRepository topicMeetingRepository;
    private final QuizResponseRepository quizResponseRepository;
    private final AssignmentResponseRepository assignmentResponseRepository;
    private final TopicFileRepository topicFileRepository;
    private final TopicLinkRepository topicLinkRepository;
    private final TopicPageRepository topicPageRepository;
    private final QuizResponseService quizResponseService;
    private final EnrollmentDetailRepository enrollmentDetailRepository;

    ObjectMapper mapper = new ObjectMapper()
            .registerModule(new ParameterNamesModule())
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule());

    public TopicDTO createTopic(TopicDTO topicDTO) {
        Topic topic = TopicMapper.toEntity(topicDTO);
        Topic createdTopic = topicRepository.save(topic);

        // check if there is data, if not then just return
        if (topicDTO.getData() == null || topicDTO.getData().isEmpty()) {
            return TopicMapper.toDTO(createdTopic);
        }

        String createdTopicData;

        switch (topic.getType()) {
            case "quiz":
                try {
                    // save the topic quiz metadata
                    TopicQuiz topicQuiz = mapper.readValue(topicDTO.getData(), TopicQuiz.class);
                    topicQuiz.setTopicId(createdTopic.getId());
                    TopicQuiz createdTopicQuiz = topicQuizRepository.save(topicQuiz);

                    // save the questions and its choices
                    topicQuiz.getQuestions().forEach(question -> {
                        question.setId(null);
                        question.setTopicQuizId(topicQuiz.getId());
                        UUID createdQuestionId = topicQuizQuestionRepository.save(question).getId();

                        question.getChoices().forEach(c -> {
                            c.setQuestionId(createdQuestionId); // Use the ID of the parent question
                            topicQuizQuestionChoiceRepository.save(c);
                        });
                    });

                    TopicQuiz finalTopicQuizSaved = topicQuizRepository
                            .findById(createdTopicQuiz.getId())
                            .orElseThrow(() -> new CustomException("Something unexpected happened!", HttpStatus.INTERNAL_SERVER_ERROR));

                    createdTopicData = mapper.writeValueAsString(finalTopicQuizSaved);
                } catch (JsonProcessingException e) {
                    throw new CustomException("Error parsing quiz data: " + e.getMessage(), HttpStatus.BAD_REQUEST);
                }
                break;
            case "assignment":
                try {
                    TopicAssignment topicAssigment = mapper.readValue(topicDTO.getData(), TopicAssignment.class);
                    topicAssigment.setId(null);
                    topicAssigment.setTopicId(createdTopic.getId());
                    TopicAssignment createdTopicAssigment = topicAssigmentRepository.save(topicAssigment);
                    createdTopicData = mapper.writeValueAsString(createdTopicAssigment);
                } catch (JsonProcessingException e) {
                    throw new CustomException("Error parsing assigment data: " + e.getMessage(), HttpStatus.BAD_REQUEST);
                }
                break;
            case "meeting":
                try {
                    TopicMeeting topicMeeting = mapper.readValue(topicDTO.getData(), TopicMeeting.class);
                    topicMeeting.setId(null);
                    topicMeeting.setTopicId(createdTopic.getId());
                    TopicMeeting createdTopicMeeting = topicMeetingRepository.save(topicMeeting);
                    createdTopicData = mapper.writeValueAsString(createdTopicMeeting);
                } catch (JsonProcessingException e) {
                    throw new CustomException("Error parsing meeting data: " + e.getMessage(), HttpStatus.BAD_REQUEST);
                }
                break;
            case "file":
                try {
                    TopicFile topicFile = mapper.readValue(topicDTO.getData(), TopicFile.class);
                    topicFile.setId(null);
                    topicFile.setTopicId(createdTopic.getId());
                    TopicFile createdTopicFile = topicFileRepository.save(topicFile);
                    createdTopicData = mapper.writeValueAsString(createdTopicFile);
                } catch (JsonProcessingException e) {
                    throw new CustomException("Error parsing file data: " + e.getMessage(), HttpStatus.BAD_REQUEST);
                }
                break;
            case "link":
                try {
                    TopicLink topicLink = mapper.readValue(topicDTO.getData(), TopicLink.class);
                    topicLink.setId(null);
                    topicLink.setTopicId(createdTopic.getId());
                    TopicLink createdTopicLink = topicLinkRepository.save(topicLink);
                    createdTopicData = mapper.writeValueAsString(createdTopicLink);
                } catch (JsonProcessingException e) {
                    throw new CustomException("Error parsing link data: " + e.getMessage(), HttpStatus.BAD_REQUEST);
                }
                break;
            case "page":
                try {
                    TopicPage topicPage = mapper.readValue(topicDTO.getData(), TopicPage.class);
                    topicPage.setId(null);
                    topicPage.setTopicId(createdTopic.getId());
                    TopicPage createdTopicPage = topicPageRepository.save(topicPage);
                    createdTopicData = mapper.writeValueAsString(createdTopicPage);
                } catch (JsonProcessingException e) {
                    throw new CustomException("Error parsing page data: " + e.getMessage(), HttpStatus.BAD_REQUEST);
                }
                break;
            default:
                throw new CustomException("Topic type not found!", HttpStatus.BAD_REQUEST);
        }

        TopicDTO createdTopicDTO = TopicMapper.toDTO(createdTopic);
        createdTopicDTO.setData(createdTopicData);

        return createdTopicDTO;
    }

    @Transactional
    public TopicDTO updateTopic(TopicDTO topicDTO) {
        if (!topicRepository.existsById(topicDTO.getId())) {
            throw new CustomException("No topic found!", HttpStatus.NOT_FOUND);
        }
        Topic updatedTopic = topicRepository.save(TopicMapper.toEntity(topicDTO));

        // check if there is data, if not then just return
        if (topicDTO.getData() == null || topicDTO.getData().isEmpty()) {
            return TopicMapper.toDTO(updatedTopic);
        }

        TopicDTO updatedTopicDTO = TopicMapper.toDTO(updatedTopic);

        switch (topicDTO.getType().toLowerCase()) {
            case "quiz":
                try {
                    TopicQuiz topicQuiz = mapper.readValue(topicDTO.getData(), TopicQuiz.class);

                    // Load existing TopicQuiz or throw an exception if not found
                    TopicQuiz existingQuiz = topicQuizRepository.findById(topicQuiz.getId())
                            .orElseThrow(() -> new CustomException("No topic quiz found!", HttpStatus.NOT_FOUND));

                    // Clear the existing questions
                    existingQuiz.getQuestions().clear();

                    // Prepare and add new questions
                    for (TopicQuizQuestion question : topicQuiz.getQuestions()) {
                        question.setId(null); // Mark as new entity
                        question.setTopicQuizId(existingQuiz.getId());

                        // Detach choices temporarily
                        List<TopicQuizQuestionChoice> choices = question.getChoices();
                        question.setChoices(new ArrayList<>());

                        // Save the question to generate its ID
                        TopicQuizQuestion savedQuestion = topicQuizQuestionRepository.save(question);

                        // Reattach choices
                        for (TopicQuizQuestionChoice choice : choices) {
                            choice.setId(null); // Mark as new entity
                            choice.setQuestionId(savedQuestion.getId());
                            savedQuestion.getChoices().add(choice);
                        }

                        // Add the saved question back to the quiz
                        existingQuiz.getQuestions().add(savedQuestion);
                    }

                    // Save the updated TopicQuiz
                    topicQuizRepository.save(existingQuiz);

                } catch (JsonProcessingException e) {
                    throw new CustomException("Error parsing quiz data: " + e.getMessage(), HttpStatus.BAD_REQUEST);
                } catch (Exception e) {
                    throw new CustomException("Something went wrong: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                }

                break;
            case "assignment":
                try {
                    TopicAssignment topicAssigment = mapper.readValue(topicDTO.getData(), TopicAssignment.class);
                    if (!topicAssigmentRepository.existsById(topicAssigment.getId())) {
                        throw new CustomException("Assigment not found!", HttpStatus.NOT_FOUND);
                    }
                    TopicAssignment createdTopicAssigment = topicAssigmentRepository.save(topicAssigment);
                    updatedTopicDTO.setData(mapper.writeValueAsString(createdTopicAssigment));
                } catch (JsonProcessingException e) {
                    throw new CustomException("Error parsing assigment data: " + e.getMessage(), HttpStatus.BAD_REQUEST);
                }

                break;
            case "meeting":
                try {
                    TopicMeeting topicMeeting = mapper.readValue(topicDTO.getData(), TopicMeeting.class);
                    if (!topicMeetingRepository.existsById(topicMeeting.getId())) {
                        throw new CustomException("Assigment not found!", HttpStatus.NOT_FOUND);
                    }
                    TopicMeeting updatedTopicMeeting = topicMeetingRepository.save(topicMeeting);
                    updatedTopicDTO.setData(mapper.writeValueAsString(updatedTopicMeeting));
                } catch (JsonProcessingException e) {
                    throw new CustomException("Error parsing meeting data: " + e.getMessage(), HttpStatus.BAD_REQUEST);
                }
                break;
            case "file":
                try {
                    TopicFile topicFile = mapper.readValue(topicDTO.getData(), TopicFile.class);
                    if (!topicFileRepository.existsById(topicFile.getId())) {
                        throw new CustomException("Assigment not found!", HttpStatus.NOT_FOUND);
                    }
                    TopicFile updatedTopicFile = topicFileRepository.save(topicFile);
                    updatedTopicDTO.setData(mapper.writeValueAsString(updatedTopicFile));
                } catch (JsonProcessingException e) {
                    throw new CustomException("Error parsing file data: " + e.getMessage(), HttpStatus.BAD_REQUEST);
                }
                break;
            case "link":
                try {
                    TopicLink topicLink = mapper.readValue(topicDTO.getData(), TopicLink.class);
                    if (!topicLinkRepository.existsById(topicLink.getId())) {
                        throw new CustomException("Assigment not found!", HttpStatus.NOT_FOUND);
                    }
                    TopicLink updatedTopicLink = topicLinkRepository.save(topicLink);
                    updatedTopicDTO.setData(mapper.writeValueAsString(updatedTopicLink));
                } catch (JsonProcessingException e) {
                    throw new CustomException("Error parsing link data: " + e.getMessage(), HttpStatus.BAD_REQUEST);
                }
                break;
            case "page":
                try {
                    TopicPage topicPage = mapper.readValue(topicDTO.getData(), TopicPage.class);
                    if (!topicPageRepository.existsById(topicPage.getId())) {
                        throw new CustomException("Assigment not found!", HttpStatus.NOT_FOUND);
                    }
                    TopicPage updateTopicPage = topicPageRepository.save(topicPage);
                    updatedTopicDTO.setData(mapper.writeValueAsString(updateTopicPage));
                } catch (JsonProcessingException e) {
                    throw new CustomException("Error parsing link data: " + e.getMessage(), HttpStatus.BAD_REQUEST);
                }
                break;
            default:
                throw new CustomException("Topic type not found!", HttpStatus.BAD_REQUEST);
        }

        return updatedTopicDTO;
    }

    public void deleteTopic(UUID id) {
        topicRepository.deleteById(id);
    }

    public TopicDTO getTopicById(UUID id, UUID courseId, UUID userId) {
        Topic topic = topicRepository.findById(id).orElseThrow(() -> new CustomException("No topic found!", HttpStatus.NOT_FOUND));
        String topicData;
        String studentResponseData = null;
        Number studentCount = null;

        switch (topic.getType()) {
            case "quiz":
                TopicQuiz topicQuiz = topicQuizRepository.findByTopicId(topic.getId()).orElseThrow(() -> new CustomException("No topic quiz found!", HttpStatus.NOT_FOUND));
                try {
                    topicData = mapper.writeValueAsString(topicQuiz);
                    List<QuizResponse> res = quizResponseRepository.findByTopicIdAndStudentId(topicQuiz.getTopicId(), userId);
                    LocalDateTime closeDate = topicQuiz.getClose() == null ? LocalDateTime.of(3000, 12,31, 23, 59, 59) : TimeUtils.convertStringToLocalDateTime(topicQuiz.getClose());
                    studentCount = enrollmentDetailRepository.countByCourseIdAndJoinDateLessThanEqual(courseId, closeDate);
                    studentResponseData = mapper.writeValueAsString(res);
                } catch (JsonProcessingException e) {
                    throw new CustomException("Error parsing quiz data: " + e.getMessage(), HttpStatus.BAD_REQUEST);
                }
                break;
            case "assignment":
                TopicAssignment topicAssignment = topicAssigmentRepository.findByTopicId(topic.getId()).orElseThrow(() -> new CustomException("No assignment found!", HttpStatus.NOT_FOUND));
                try {
                    topicData = mapper.writeValueAsString(topicAssignment);
                    Optional<AssignmentResponse> res = assignmentResponseRepository.findByTopicIdAndStudentId(topicAssignment.getTopicId(), userId);
                    LocalDateTime closeDate = topicAssignment.getClose() == null ? TimeUtils.MAX : TimeUtils.convertStringToLocalDateTime(topicAssignment.getClose());
                    studentCount = enrollmentDetailRepository.countByCourseIdAndJoinDateLessThanEqual(courseId, closeDate);
                    if (res.isPresent()) studentResponseData = mapper.writeValueAsString(res);
                } catch (JsonProcessingException e) {
                    throw new CustomException("Error parsing assigment data: " + e.getMessage(), HttpStatus.BAD_REQUEST);
                }
                break;
            case "meeting":
                TopicMeeting topicMeeting = topicMeetingRepository.findByTopicId(topic.getId()).orElseThrow(() -> new CustomException("No meeting found!", HttpStatus.NOT_FOUND));
                try {
                    topicData = mapper.writeValueAsString(topicMeeting);
                    // meeting does not have closeDate
                    LocalDateTime closeDate = topicMeeting.getOpen() == null ? TimeUtils.MAX : TimeUtils.convertStringToLocalDateTime(topicMeeting.getOpen());
                    studentCount = enrollmentDetailRepository.countByCourseIdAndJoinDateLessThanEqual(courseId, closeDate);
                } catch (JsonProcessingException e) {
                    throw new CustomException("Error parsing meeting data: " + e.getMessage(), HttpStatus.BAD_REQUEST);
                }
                break;
            case "file":
                TopicFile topicFile = topicFileRepository.findByTopicId(topic.getId()).orElseThrow(() -> new CustomException("No file found!", HttpStatus.NOT_FOUND));
                try {
                    topicData = mapper.writeValueAsString(topicFile);
                } catch (JsonProcessingException e) {
                    throw new CustomException("Error parsing file data: " + e.getMessage(), HttpStatus.BAD_REQUEST);
                }
                break;
            case "link":
                TopicLink topicLink = topicLinkRepository.findByTopicId(topic.getId()).orElseThrow(() -> new CustomException("No file found!", HttpStatus.NOT_FOUND));
                try {
                    topicData = mapper.writeValueAsString(topicLink);
                } catch (JsonProcessingException e) {
                    throw new CustomException("Error parsing link data: " + e.getMessage(), HttpStatus.BAD_REQUEST);
                }
                break;
            case "page":
                TopicPage topicPage = topicPageRepository.findByTopicId(topic.getId()).orElseThrow(() -> new CustomException("No page found!", HttpStatus.NOT_FOUND));
                try {
                    topicData = mapper.writeValueAsString(topicPage);
                } catch (JsonProcessingException e) {
                    throw new CustomException("Error parsing page data: " + e.getMessage(), HttpStatus.BAD_REQUEST);
                }
                break;
            default:
                throw new CustomException("Topic type not found!", HttpStatus.BAD_REQUEST);
        }

        TopicDTO createdTopicDTO = TopicMapper.toDTO(topic);
        createdTopicDTO.setData(topicData);
        createdTopicDTO.setStudentCount(studentCount);
        createdTopicDTO.setResponse(studentResponseData);

        return createdTopicDTO;
    }

    public SingleAssignmentReportDTO getSingleAssignmentReport(UUID courseId, UUID topicId) {
        Topic topic = topicRepository.findById(topicId).orElseThrow(() -> new CustomException("No topic found!", HttpStatus.NOT_FOUND));
        TopicAssignment topicAssignment = topicAssigmentRepository.findByTopicId(topic.getId()).orElseThrow(() -> new CustomException("No topic assignment found!", HttpStatus.NOT_FOUND));
        List<AssignmentResponse> assignmentResponses = assignmentResponseRepository.findAllByTopicId(topic.getId());
        LocalDateTime topicEndTime = topicAssignment.getClose() == null ? TimeUtils.MAX : TimeUtils.convertStringToLocalDateTime(topicAssignment.getClose());

        // exclude students that join after the topic is closed
        List<EnrollmentDetail> studentsThatTookPartIn = enrollmentDetailRepository.findByCourseIdAndJoinDateLessThanEqual(courseId, topicEndTime);
        int studentCount = studentsThatTookPartIn.size();
        if (studentCount == 0) {
            return new SingleAssignmentReportDTO(topic.getTitle());
        }

        Map<UUID, Pair<Double, UUID>> studentWithMarkBase10AndResponseId = assignmentResponses.stream()
                .filter(resp -> resp.getMark() != null)
                .collect(Collectors.toMap(
                        resp -> resp.getStudent().getId(),
                        resp -> new Pair<>(resp.getMark() / 10, resp.getId()) // getMark / 100 * 10
                ));

        // same as above but without the response id
        Map<UUID, Double> studentWithMarkBase10 = assignmentResponses.stream()
                .filter(resp -> resp.getMark() != null)
                .collect(Collectors.toMap(
                        resp -> resp.getStudent().getId(),
                        resp -> resp.getMark() / 10
                ));

        Map<String, Long> fileTypeCount = assignmentResponses.stream()
                .filter(res -> res.getCloudinaryFiles() != null)
                .flatMap(res -> res.getCloudinaryFiles().stream())
                .map(file -> {
                    int dotIndex = file.getName().lastIndexOf('.');
                    return file.getName().substring(dotIndex + 1);
                })
                .collect(Collectors.groupingBy(
                        ext -> ext,
                        Collectors.counting()
                ));

        List<SingleAssignmentReportDTO.StudentInfoAndMark> studentInfoAndMarks = getStudentInfoWithMarkAndResponseIdForAssignment(studentsThatTookPartIn, studentWithMarkBase10AndResponseId);

        SingleAssignmentReportDTO reportDTO = new SingleAssignmentReportDTO();
        reportDTO.setName(topic.getTitle());
        reportDTO.setStudents(studentsThatTookPartIn.stream().map(st -> UserMapper.mapToDTO(st.getStudent())).toList());
        reportDTO.setStudentMarks(studentInfoAndMarks);

        reportDTO.setStudentWithMarkOver8(studentInfoAndMarks.stream().filter(info -> info.getMark() != null && info.getMark() >= 8.0).toList());
        reportDTO.setStudentWithMarkOver5(studentInfoAndMarks.stream().filter(info -> info.getMark() != null && info.getMark() >= 5.0 && info.getMark() < 8.0).toList());
        reportDTO.setStudentWithMarkOver2(studentInfoAndMarks.stream().filter(info -> info.getMark() != null && info.getMark() >= 2.0 && info.getMark() < 5.0).toList());
        reportDTO.setStudentWithMarkOver0(studentInfoAndMarks.stream().filter(info -> info.getMark() != null && info.getMark() < 2.0).toList());
        reportDTO.setStudentWithNoResponse(studentInfoAndMarks.stream().filter(info -> !info.getSubmitted()).toList());

        reportDTO.setMarkDistributionCount(calculateMarkDistribution(studentWithMarkBase10, studentCount));
        reportDTO.setSubmissionCount(assignmentResponses.size());
        reportDTO.setGradedSubmissionCount(assignmentResponses.stream().filter(res -> res.getMark() != null).count());
        reportDTO.setFileCount(assignmentResponses.stream().mapToInt(res -> res.getCloudinaryFiles() != null ? res.getCloudinaryFiles().size() : 0).sum());
        reportDTO.setAvgMark(assignmentResponses.stream().filter(res -> res.getMark() != null).mapToDouble(AssignmentResponse::getMark).average().orElse(0.0));
        reportDTO.setMaxMark(assignmentResponses.stream().filter(res -> res.getMark() != null).mapToDouble(AssignmentResponse::getMark).max().orElse(0.0));
        reportDTO.setCompletionRate((double) assignmentResponses.size() / (double) studentCount);
        reportDTO.setStudents(studentsThatTookPartIn.stream().map(detail -> UserMapper.mapToDTO(detail.getStudent())).toList());
        reportDTO.setFileTypeCount(fileTypeCount);

        return reportDTO;
    }

    public SingleQuizReportDTO getSingleQuizReport(UUID courseId, UUID topicId) {
        Topic topic = topicRepository.findById(topicId).orElseThrow(() -> new CustomException("No topic found!", HttpStatus.NOT_FOUND));
        SingleQuizReportDTO reportDTO = new SingleQuizReportDTO();

        TopicQuiz topicQuiz = topicQuizRepository.findByTopicId(topic.getId()).orElseThrow(() -> new CustomException("No topic quiz found!", HttpStatus.NOT_FOUND));
        List<TopicQuizQuestion> quizQuestions = topicQuizQuestionRepository.findAllByTopicQuizId(topicQuiz.getId());
        List<QuizResponseDTO> quizResponses = quizResponseService.getAllQuizResponsesByTopicId(topicQuiz.getTopicId());
        LocalDateTime topicEndTime = topicQuiz.getClose() == null ? TimeUtils.MAX : TimeUtils.convertStringToLocalDateTime(topicQuiz.getClose());
        List<EnrollmentDetail> studentsThatTookPartIn = enrollmentDetailRepository.findByCourseIdAndJoinDateLessThanEqual(courseId, topicEndTime);
        int studentCount = studentsThatTookPartIn.size();

        Map<UUID, Double> marksWithStudentId = quizResponses.stream()
                .map(responseDTO -> {
                    // First calculate average mark for this response
                    double responseMark = responseDTO.getAnswers().stream()
                            .map(answer -> {
                                try {
                                    Question question = mapper.readValue(answer.getQuestion(), Question.class);
                                    return (answer.getMark() / question.getDefaultMark()) * 10; // Normalize to base 10
                                } catch (JsonProcessingException e) {
                                    throw new CustomException("Error parsing question data: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                                }
                            })
                            .mapToDouble(Double::doubleValue)
                            .average()
                            .orElse(0.0);

                    return new AbstractMap.SimpleEntry<>(responseDTO.getStudent().getId(), responseMark);
                })
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey, // Group by studentId
                        Collectors.mapping(
                                Map.Entry::getValue, // Extract the response-level marks
                                Collectors.toList() // Collect marks into a list
                        )
                ))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> calculateMark(entry.getValue(), topicQuiz.getGradingMethod())
                ));

        double avgMark = marksWithStudentId.values().stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0); // Default to 0.0 if no marks exist

        double maxMark = marksWithStudentId.values().stream()
                .mapToDouble(Double::doubleValue)
                .max()
                .orElse(0.0); // Default to 0.0 if no marks exist

        double minMark = marksWithStudentId.values().stream()
                .mapToDouble(Double::doubleValue)
                .min()
                .orElse(0.0); // Default to 0.0 if no marks exist

        List<SingleQuizReportDTO.StudentInfoAndMark> studentInfoAndMarks = getStudentInfoWithMarkAndResponseIdForQuiz(studentsThatTookPartIn, marksWithStudentId);

        reportDTO.setName(topic.getTitle());

        reportDTO.setStudentWithMark(studentInfoAndMarks);
        reportDTO.setStudentWithMarkOver8(studentInfoAndMarks.stream().filter(info -> info.getSubmitted() && info.getMark() != null && info.getMark() >= 8.0).toList());
        reportDTO.setStudentWithMarkOver5(studentInfoAndMarks.stream().filter(info -> info.getSubmitted() && info.getMark() != null && info.getMark() >= 5.0 && info.getMark() < 8.0).toList());
        reportDTO.setStudentWithMarkOver2(studentInfoAndMarks.stream().filter(info -> info.getSubmitted() && info.getMark() != null && info.getMark() >= 2.0 && info.getMark() < 5.0).toList());
        reportDTO.setStudentWithMarkOver0(studentInfoAndMarks.stream().filter(info -> info.getSubmitted() && info.getMark() != null && info.getMark() < 2.0).toList());
        reportDTO.setStudentWithNoResponse(studentInfoAndMarks.stream().filter(info -> !info.getSubmitted()).toList());

        reportDTO.setMarkDistributionCount(calculateMarkDistribution(marksWithStudentId, studentCount));
        reportDTO.setQuestionCount(topicQuiz.getQuestions().size());
        reportDTO.setMaxDefaultMark(topicQuiz.getQuestions().stream().mapToDouble(TopicQuizQuestion::getDefaultMark).sum());
        reportDTO.setAvgStudentMarkBase10(avgMark);
        reportDTO.setMaxStudentMarkBase10(maxMark);
        reportDTO.setMinStudentMarkBase10(minMark);
        reportDTO.setAttemptCount(quizResponses.size());
        reportDTO.setAvgTimeSpend(calculateAvgTimeSpend(quizResponses));
        reportDTO.setCompletionRate(((double)marksWithStudentId.entrySet().size()) / ((double)studentCount));
        reportDTO.setStudents(studentsThatTookPartIn.stream().map(detail -> UserMapper.mapToDTO(detail.getStudent())).toList());
        reportDTO.setTrueFalseQuestionCount(countQuestionType(quizQuestions, "True/False"));
        reportDTO.setMultipleChoiceQuestionCount(countQuestionType(quizQuestions, "Choices Answer"));
        reportDTO.setShortAnswerQuestionCount(countQuestionType(quizQuestions, "Short Answer"));

        return reportDTO;
    }

    private double calculateMark(List<Double> marks, String method) {
        return switch (method) {
            case "Highest Grade" -> marks.stream().max(Double::compare).orElse(0.0);
            case "Average Grade" -> marks.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            case "First Grade" -> marks.isEmpty() ? 0.0 : marks.get(0);
            case "Last Grade" -> marks.isEmpty() ? 0.0 : marks.get(marks.size() - 1);
            default -> throw new IllegalArgumentException("Invalid method: " + method);
        };
    }

    private double calculateAvgTimeSpend(List<QuizResponseDTO> quizResponses) {
        return quizResponses
                .stream()
                .mapToDouble(res -> (TimeUtils.convertStringToLocalDateTime(res.getCompletedAt()).getSecond() - TimeUtils.convertStringToLocalDateTime(res.getStartedAt()).getSecond()))
                .average()
                .orElse(0.0);
    }

    private Number countQuestionType(List<TopicQuizQuestion> questions, String questionType) {
        return questions.stream()
                .mapToInt(q -> q.getType().equals(questionType) ? 1 : 0)
                .sum();
    }

    public Map<Number, Number> calculateMarkDistribution(Map<UUID, Double> studentsWithMark, int studentCount) {
        List<Double> allMarks = studentsWithMark.values().stream().toList();

        // Count how many marks fall into each range
        long count8OrMore = allMarks.stream().filter(mark -> mark >= 8).count();
        long count5To7 = allMarks.stream().filter(mark -> mark >= 5 && mark < 8.0).count();
        long count2To4 = allMarks.stream().filter(mark -> mark >= 2 && mark < 5.0).count();
        long count0To1 = allMarks.stream().filter(mark -> mark >= 0.0 && mark < 2.0).count();

        Map<Number, Number> percentageMap = new HashMap<>();
        percentageMap.put(8, count8OrMore);
        percentageMap.put(5, count5To7);
        percentageMap.put(2, count2To4);
        percentageMap.put(0, count0To1);
        percentageMap.put(-1, studentCount - count0To1 - count2To4 - count5To7 - count8OrMore);

        return percentageMap;
    }

    public List<SingleQuizReportDTO.StudentInfoAndMark> getStudentInfoWithMarkAndResponseIdForQuiz(
            List<EnrollmentDetail> studentsThatTookPartIn,
            Map<UUID, Double> marksWithStudentId
    ) {
        // First, create a map of enrollment details by student ID for easier lookup
        Map<UUID, EnrollmentDetail> enrollmentByStudentId = studentsThatTookPartIn.stream()
                .collect(Collectors.toMap(
                        detail -> detail.getStudent().getId(),
                        detail -> detail
                ));

        // Create StudentInfoAndMark objects for students with marks
        List<SingleQuizReportDTO.StudentInfoAndMark> studentsWithMarks = marksWithStudentId.entrySet().stream()
                .map(entry -> {
                    UUID studentId = entry.getKey();
                    Double mark = entry.getValue();
                    EnrollmentDetail enrollment = enrollmentByStudentId.get(studentId);

                    SingleQuizReportDTO.StudentInfoAndMark info = new SingleQuizReportDTO.StudentInfoAndMark();
                    info.setStudent(UserMapper.mapToDTO(enrollment.getStudent()));
                    info.setMark(mark);
                    info.setResponseId(null); // cause there can be multiple quizzes, i dont know what to get
                    return info;
                })
                .toList();

        // Create StudentInfoAndMark objects for students with no response
        List<SingleQuizReportDTO.StudentInfoAndMark> studentsNoResponse = enrollmentByStudentId.entrySet().stream()
                .filter(entry -> !marksWithStudentId.containsKey(entry.getKey()))
                .map(entry -> {
                    SingleQuizReportDTO.StudentInfoAndMark info = new SingleQuizReportDTO.StudentInfoAndMark();
                    info.setStudent(UserMapper.mapToDTO(entry.getValue().getStudent()));
                    info.setSubmitted(false);
                    info.setMark(0.0); // or null, depending on your requirements
                    info.setResponseId(null);

                    return info;
                })
                .toList();

        // Now categorize all students based on their marks
        List<SingleQuizReportDTO.StudentInfoAndMark> allStudents = new ArrayList<>(studentsWithMarks);
        allStudents.addAll(studentsNoResponse);
        return allStudents;
    }

    public List<SingleAssignmentReportDTO.StudentInfoAndMark> getStudentInfoWithMarkAndResponseIdForAssignment(
            List<EnrollmentDetail> studentsThatTookPartIn,
            Map<UUID, Pair<Double, UUID>> studentIdWithMarkBase10AndResponseId
    ) {
        // First, create a map of enrollment details by student ID for easier lookup
        Map<UUID, EnrollmentDetail> enrollmentByStudentId = studentsThatTookPartIn.stream()
                .collect(Collectors.toMap(
                        detail -> detail.getStudent().getId(),
                        detail -> detail
                ));

        // Create StudentInfoAndMark objects for students with marks
        List<SingleAssignmentReportDTO.StudentInfoAndMark> studentsWithMarks = studentIdWithMarkBase10AndResponseId.entrySet().stream()
                .map(entry -> {
                    UUID studentId = entry.getKey();
                    Double mark = entry.getValue().a;
                    EnrollmentDetail enrollment = enrollmentByStudentId.get(studentId);

                    SingleAssignmentReportDTO.StudentInfoAndMark info = new SingleAssignmentReportDTO.StudentInfoAndMark();
                    info.setStudent(UserMapper.mapToDTO(enrollment.getStudent()));
                    info.setMark(mark);
                    info.setResponseId(entry.getValue().b); // cause there can be multiple quizzes, i dont know what to get
                    return info;
                })
                .toList();

        // Create StudentInfoAndMark objects for students with no response
        List<SingleAssignmentReportDTO.StudentInfoAndMark> studentsNoResponse = enrollmentByStudentId.entrySet().stream()
                .filter(entry -> !studentIdWithMarkBase10AndResponseId.containsKey(entry.getKey()))
                .map(entry -> {
                    SingleAssignmentReportDTO.StudentInfoAndMark info = new SingleAssignmentReportDTO.StudentInfoAndMark();
                    info.setStudent(UserMapper.mapToDTO(entry.getValue().getStudent()));
                    info.setSubmitted(false);
                    info.setMark(0.0); // or null, depending on your requirements
                    info.setResponseId(null);

                    return info;
                })
                .toList();

        // Now categorize all students based on their marks
        List<SingleAssignmentReportDTO.StudentInfoAndMark> allStudents = new ArrayList<>(studentsWithMarks);
        allStudents.addAll(studentsNoResponse);
        return allStudents;
    }
}
