package com.letslive.letslearnbackend.dto;

import com.letslive.letslearnbackend.entities.SingleAssignmentReportDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllAssignmentsReportDTO {
    private Number assignmentCount;

    private Double avgMark;
    private Double avgCompletionRate;
    private Number numberOfAssignmentEndsAtThisMonth;
    private LocalDateTime closestNextEndAssignment;

    private Map<Number, Double> markDistributionByPercentage;
    private Map<UUID, Double> studentMarks;
    private Map<String, Long> fileTypeCount;
    private List<SingleAssignmentReportDTO> singleAssignmentReports;
}
