package com.letslive.letslearnbackend.mappers;

import com.letslive.letslearnbackend.dto.CourseDTO;
import com.letslive.letslearnbackend.entities.Course;

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
            builder.sections(course.getSections().stream().map(section -> SectionMapper.mapToDTO(section)).toList());
        }

        if (course.getCreator() != null) {
                builder.creator(UserMapper.mapToDTO(course.getCreator()));
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