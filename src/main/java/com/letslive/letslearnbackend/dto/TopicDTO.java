package com.letslive.letslearnbackend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class TopicDTO {
    private UUID id;
    private UUID sectionId;
    private String title;
    private String type;
    private String data;
    private String response;
    private CourseDTO course;
}

