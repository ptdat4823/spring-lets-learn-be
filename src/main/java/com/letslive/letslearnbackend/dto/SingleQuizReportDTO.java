package com.letslive.letslearnbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class SingleQuizReportDTO {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScoreByPercentage {
        private Double score;
        private Double percentage;
    }

    private Number questionCount;
    private Double avgMark;
    private Double topMark;
    private Double completionRate;
    private Number studentCount;
    private List<ScoreByPercentage> scoresByPercentage;
    private Map<String, Double> questionTypeByPercentage;
}
