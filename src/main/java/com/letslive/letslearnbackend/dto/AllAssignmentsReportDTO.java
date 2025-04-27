package com.letslive.letslearnbackend.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.letslive.letslearnbackend.entities.SingleAssignmentReportDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllAssignmentsReportDTO {
    @Data
    @AllArgsConstructor
    public static class StudentInfoWithAverageMark {
        UserDTO user;
        Double averageMark;
        Boolean submitted;
    }

    private Number assignmentCount;

    private Double avgMark;
    private Double avgCompletionRate;
    private Number numberOfAssignmentEndsAtThisMonth;
    private LocalDateTime closestNextEndAssignment;

    private Map<Number, Number> markDistributionCount;
    @JsonIgnore
    private List<StudentInfoWithAverageMark> studentInfoWithMarkAverage;
    private List<StudentInfoWithAverageMark> studentWithMarkOver8;
    private List<StudentInfoWithAverageMark> studentWithMarkOver5;
    private List<StudentInfoWithAverageMark> studentWithMarkOver2;
    private List<StudentInfoWithAverageMark> studentWithMarkOver0;
    private List<StudentInfoWithAverageMark> studentWithNoResponse;

    private Map<String, Long> fileTypeCount;
    private List<SingleAssignmentReportDTO> singleAssignmentReports;
}
