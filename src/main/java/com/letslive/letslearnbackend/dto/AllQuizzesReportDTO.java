package com.letslive.letslearnbackend.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private Double avgCompletionPercentage;

    private Number minQuestionCount;
    private Number maxQuestionCount;
    private Double minStudentScoreBase10;
    private Double maxStudentScoreBase10;

    @JsonIgnore
    private List<SingleQuizReportDTO.StudentInfoAndMark> studentInfoWithMarkAverage;

    private List<SingleQuizReportDTO.StudentInfoAndMark> studentWithMarkOver8;
    private List<SingleQuizReportDTO.StudentInfoAndMark> studentWithMarkOver5;
    private List<SingleQuizReportDTO.StudentInfoAndMark> studentWithMarkOver2;
    private List<SingleQuizReportDTO.StudentInfoAndMark> studentWithMarkOver0;
    private List<SingleQuizReportDTO.StudentInfoAndMark> studentWithNoResponse;

    private Map<Number, Number> markDistributionCount;

    private List<SingleQuizReportDTO> singleQuizReports;

    private Number trueFalseQuestionCount;
    private Number multipleChoiceQuestionCount;
    private Number shortAnswerQuestionCount;
}