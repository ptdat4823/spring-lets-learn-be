package com.letslive.letslearnbackend.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SingleAssignmentReportDTO {
    private String name;
    private Map<UUID, Double> studentMarks = new HashMap<>();

    private Map<Number, Double> markDistributionByPercentage = new HashMap<>();

    private Number submissionCount = 0;
    private Number gradedSubmissionCount = 0;
    private Number fileCount = 0;

    private Double avgMark = 0.0;
    private Double maxMark = 0.0;
    private Double completionRate = 0.0;

    // count even students that don't take part in the assignment
    private Number studentCount = 0;
    private Map<String, Long> fileTypeCount = new HashMap<>();

    public SingleAssignmentReportDTO(String name) {
        this.name = name;
        markDistributionByPercentage.put(-1, 0.0);
        markDistributionByPercentage.put(0, 0.0);
        markDistributionByPercentage.put(2, 0.0);
        markDistributionByPercentage.put(5, 0.0);
        markDistributionByPercentage.put(8, 0.0);
    }
}
