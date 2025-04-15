package com.letslive.letslearnbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class StudentWorksInACourseDTO {
    private CourseDTO course;
    private List<String> data;
}
