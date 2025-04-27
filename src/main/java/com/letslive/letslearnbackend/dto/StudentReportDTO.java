package com.letslive.letslearnbackend.dto;

import lombok.Data;
import org.antlr.v4.runtime.misc.Pair;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class StudentReportDTO {
    private List<Pair<LocalDateTime, Double>> daysWithAvgQuizMark;
    private List<Pair<LocalDateTime, Double>> daysWithAvgAssignmentMark;

    private Number totalQuizCount;
    private Number totalAssignmentCount;

    private Number quizToDo;
    private Number assignmentToDo;

    private Number avgQuizMark;
    private Number avgAssignmentMark;

    private List<Pair<String, Double>> topTopicQuizWithMark;
    private List<Pair<String, Double>> topTopicAssignmentWithMark;
}
