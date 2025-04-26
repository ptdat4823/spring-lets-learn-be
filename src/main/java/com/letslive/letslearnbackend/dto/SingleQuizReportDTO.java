package com.letslive.letslearnbackend.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    private List<StudentInfoAndMark> studentWithMark;

    private List<StudentInfoAndMark> studentWithMarkOver8;
    private List<StudentInfoAndMark> studentWithMarkOver5;
    private List<StudentInfoAndMark> studentWithMarkOver2;
    private List<StudentInfoAndMark> studentWithMarkOver0;
    private List<StudentInfoAndMark> studentWithNoResponse;

    private Double maxDefaultMark;

    private Map<Number, Number> markDistributionCount;

    private Number questionCount;

    private Double avgStudentMarkBase10;
    private Double maxStudentMarkBase10;
    private Double minStudentMarkBase10;

    private Number attemptCount;
    private Number avgTimeSpend;

    private Double completionRate;

    // count even students that don't take part in the quiz
    private List<UserDTO> students;

    private Number trueFalseQuestionCount;
    private Number multipleChoiceQuestionCount;
    private Number shortAnswerQuestionCount;
}
