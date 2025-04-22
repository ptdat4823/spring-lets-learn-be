package com.letslive.letslearnbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllQuizzesReportDTO {
    private Number quizCount;
    private Double completionPercentage;
    private Double avgMark;
    private Map<String, Double> questionTypeByPercentage;
    private List<SingleQuizReportDTO.ScoreByPercentage> scoreByPercentage;

    private List<SingleQuizReportDTO> singleQuizReports;
}