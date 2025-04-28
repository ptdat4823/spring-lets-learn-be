package com.letslive.letslearnbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class StudentReportDTO {
    @Data
    @AllArgsConstructor
    public static class TopicInfo {
        TopicDTO topic;
        UUID responseId;
        Double mark;
        String doneTime;
    }

    private Number totalQuizCount;
    private Number totalAssignmentCount;

    private Number quizToDoCount;
    private Number assignmentToDoCount;

    private Number avgQuizMark;
    private Number avgAssignmentMark;

    private List<TopicInfo> topTopicQuiz;
    private List<TopicInfo> topTopicAssignment;
}
