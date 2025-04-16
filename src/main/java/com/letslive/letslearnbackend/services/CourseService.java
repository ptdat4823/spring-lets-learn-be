package com.letslive.letslearnbackend.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.letslive.letslearnbackend.dto.CourseDTO;
import com.letslive.letslearnbackend.dto.TopicDTO;
import com.letslive.letslearnbackend.entities.Course;
import com.letslive.letslearnbackend.entities.User;
import com.letslive.letslearnbackend.exception.CustomException;
import com.letslive.letslearnbackend.mappers.CourseMapper;
import com.letslive.letslearnbackend.mappers.TopicMapper;
import com.letslive.letslearnbackend.repositories.*;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final TopicQuizRepository topicQuizRepository;
    private final ObjectMapper mapper = new ObjectMapper();
    private final TopicAssigmentRepository topicAssigmentRepository;
    private final TopicMeetingRepository topicMeetingRepository;

    public List<CourseDTO> getAllCoursesByUserID(UUID userID) {
        userRepository
                .findById(userID)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        return courseRepository.findByCreatorId(userID).stream().map(CourseMapper::mapToDTO).toList();
    }

    public CourseDTO getCourse(UUID id) {
        Course course = courseRepository
                .findById(id)
                .orElseThrow(() -> new CustomException("Course not found", HttpStatus.NOT_FOUND));

        return CourseMapper.mapToDTO(course);
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

        courseRepository.save(course);
        userRepository.save(user);
    }

    public List<TopicDTO> getAllWorksOfCourse(UUID courseId, String type) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new CustomException("Course not found", HttpStatus.NOT_FOUND));
        List<TopicDTO> result = new ArrayList<>();

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
                                    try {
                                        String data = mapper.writeValueAsString(topicAssignment);
                                        TopicDTO topicDTO = TopicMapper.toDTO(topicSection);
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
}
