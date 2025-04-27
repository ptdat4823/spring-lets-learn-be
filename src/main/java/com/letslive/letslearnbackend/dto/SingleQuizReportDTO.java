package com.letslive.letslearnbackend.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

// everything is calculated on base 10 except for maxDefaultMark
@Data
@NoArgsConstructor
public class SingleQuizReportDTO {
    @Data
    public static class StudentInfoAndMark {
        private UserDTO student;
        private Boolean submitted = true; // mark will be 0.0 for BOTH student who did bad and student who did not turn in the quiz, use submitted to know if student has submitted
        private Double mark;
        private UUID responseId;
    }

    private String name;

    @JsonIgnore // below is for the all quizzes calculation
    private List<StudentInfoAndMark> studentWithMark = new ArrayList<>();

    private List<StudentInfoAndMark> studentWithMarkOver8 = new ArrayList<>();
    private List<StudentInfoAndMark> studentWithMarkOver5 = new ArrayList<>();
    private List<StudentInfoAndMark> studentWithMarkOver2 = new ArrayList<>();
    private List<StudentInfoAndMark> studentWithMarkOver0 = new ArrayList<>();
    private List<StudentInfoAndMark> studentWithNoResponse = new ArrayList<>();

    private Double maxDefaultMark = 0.0;

    private Map<Number, Number> markDistributionCount = new HashMap<>();

    private Number questionCount = 0;

    private Double avgStudentMarkBase10 = 0.0;
    private Double maxStudentMarkBase10 = 0.0;
    private Double minStudentMarkBase10 = 0.0;

    private Number attemptCount = 0;
    private Number avgTimeSpend = 0;

    private Double completionRate = 0.0;

    // count even students that don't take part in the quiz
    private List<UserDTO> students = new ArrayList<>();

    private Number trueFalseQuestionCount = 0;
    private Number multipleChoiceQuestionCount = 0;
    private Number shortAnswerQuestionCount = 0;
}
