package com.letslive.letslearnbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllQuizzesReportDTO {
    private Number quizCount;
    private Double avgCompletionPercentage;

    private Number minQuestionCount;
    private Number maxQuestionCount;
    private Double minStudentScoreBase10;
    private Double maxStudentScoreBase10;

    private Map<UUID, Double> studentMarkPercentages;
    private Map<Number, Double> markDistributionByPercentage;

    private List<SingleQuizReportDTO> singleQuizReports;

    private Number trueFalseQuestionCount;
    private Number multipleChoiceQuestionCount;
    private Number shortAnswerQuestionCount;
}