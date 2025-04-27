package com.letslive.letslearnbackend.entities;

import com.letslive.letslearnbackend.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SingleAssignmentReportDTO {
    @Data
    public static class StudentInfoAndMark {
        private UserDTO student;
        private Boolean submitted = true; // mark will be 0.0 for BOTH student who did bad and student who did not turn in the quiz, use submitted to know if student has submitted
        private Double mark;
        private UUID responseId;
    }

    private String name;

    private List<StudentInfoAndMark> studentMarks = new ArrayList<>();
    private List<StudentInfoAndMark> studentWithMarkOver8 = new ArrayList<>();
    private List<StudentInfoAndMark> studentWithMarkOver5 = new ArrayList<>();
    private List<StudentInfoAndMark> studentWithMarkOver2 = new ArrayList<>();
    private List<StudentInfoAndMark> studentWithMarkOver0 = new ArrayList<>();
    private List<StudentInfoAndMark> studentWithNoResponse = new ArrayList<>();

    private Map<Number, Number> markDistributionCount = new HashMap<>();

    private Number submissionCount = 0;
    private Number gradedSubmissionCount = 0;
    private Number fileCount = 0;

    private Double avgMark = 0.0;
    private Double maxMark = 0.0;
    private Double completionRate = 0.0;

    // count even students that don't take part in the assignment
    private List<UserDTO> students = new ArrayList<>();
    private Map<String, Long> fileTypeCount = new HashMap<>();

    public SingleAssignmentReportDTO(String name) {
        this.name = name;
        markDistributionCount = new HashMap<>();
        markDistributionCount.put(-1, 0);
        markDistributionCount.put(0, 0);
        markDistributionCount.put(2, 0);
        markDistributionCount.put(5, 0);
        markDistributionCount.put(8, 0);
    }
}
