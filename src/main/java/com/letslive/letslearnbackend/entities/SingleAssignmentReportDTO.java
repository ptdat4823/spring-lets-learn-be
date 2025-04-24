package com.letslive.letslearnbackend.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SingleAssignmentReportDTO {
    private String name;
    private Map<UUID, Double> studentMarks;

    private Map<Number, Double> markDistributionByPercentage;

    private Number submissionCount;
    private Number gradedSubmissionCount;
    private Number fileCount;

    private Double avgMark;
    private Double maxMark;
    private Double completionRate;

    // count even students that don't take part in the assignment
    private Number studentCount;
    private Map<String, Long> fileTypeCount;
}
