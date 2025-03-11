package com.letslive.letslearnbackend.mappers;

import com.letslive.letslearnbackend.dto.CourseDTO;
import com.letslive.letslearnbackend.entities.Course;

public class CourseMapper {
    public static CourseDTO mapToDTO(Course course) {
        return CourseDTO
                .builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .imageUrl(course.getImageUrl())
                .category(course.getCategory())
                .level(course.getLevel())
                .isPublished(course.getIsPublished())
                .creatorId(course.getCreator().getId())
                .build();
    }

    public static Course mapToEntity(CourseDTO courseDTO) {
        return Course
                .builder()
                .id(courseDTO.getId())
                .title(courseDTO.getTitle())
                .description(courseDTO.getDescription())
                .imageUrl(courseDTO.getImageUrl())
                .category(courseDTO.getCategory())
                .level(courseDTO.getLevel())
                .isPublished(courseDTO.getIsPublished())
                .build();
    }
}