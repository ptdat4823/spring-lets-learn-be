package com.letslive.letslearnbackend.mappers;

import com.letslive.letslearnbackend.dto.CourseDTO;
import com.letslive.letslearnbackend.entities.Course;
import com.letslive.letslearnbackend.entities.EnrollmentDetail;

public class CourseMapper {
    public static CourseDTO mapToDTO(Course course) {
        CourseDTO.CourseDTOBuilder builder = CourseDTO
                .builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .imageUrl(course.getImageUrl())
                .category(course.getCategory())
                .level(course.getLevel())
                .isPublished(course.getIsPublished());

        if (course.getSections() != null) {
            builder.sections(course.getSections().stream().map(SectionMapper::mapToDTO).toList());
        }

        if (course.getCreator() != null) {
                builder.creator(UserMapper.mapToDTO(course.getCreator()));
        }

        if (course.getEnrollments() != null) {
            // avoid stackoverflow
            //course.getStudents().forEach(student -> student.setCourses(null));
            builder.students(course.getEnrollments().stream().map(EnrollmentDetail::getStudent).map(UserMapper::mapToDTO).toList());
        }

        return builder.build();
    }

    public static Course mapToEntity(CourseDTO courseDTO) {
        Course.CourseBuilder builder = Course
                .builder()
                .id(courseDTO.getId())
                .title(courseDTO.getTitle())
                .description(courseDTO.getDescription())
                .imageUrl(courseDTO.getImageUrl())
                .category(courseDTO.getCategory())
                .level(courseDTO.getLevel())
                .isPublished(courseDTO.getIsPublished());

        if (courseDTO.getCreator() != null) {
            builder.creator(UserMapper.mapToEntity(courseDTO.getCreator()));
        }

        return builder.build();
    }
}