package com.letslive.letslearnbackend.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

// everything is calculated on base 10 except for maxDefaultMark
@Data
@NoArgsConstructor
public class SingleQuizReportDTO {
    private String name;
    @JsonIgnore // the two below are for the all quizzes calculation
    private Map<UUID, Double> studentWithMark;
    private Double maxDefaultMark;

    private Map<Number, Double> markDistributionByPercentage;

    private Number questionCount;

    private Double avgStudentMarkBase10;
    private Double maxStudentMarkBase10;
    private Double minStudentMarkBase10;

    private Number attemptCount;
    private Number avgTimeSpend;

    private Double completionRate;

    // count even students that don't take part in the quiz
    private Number studentCount;

    private Number trueFalseQuestionCount;
    private Number multipleChoiceQuestionCount;
    private Number shortAnswerQuestionCount;
}
