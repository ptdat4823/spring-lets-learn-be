package com.letslive.letslearnbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class StudentWorksInACourseDTO {
    private UUID courseId;
    private List<TopicDTO> data;
}
