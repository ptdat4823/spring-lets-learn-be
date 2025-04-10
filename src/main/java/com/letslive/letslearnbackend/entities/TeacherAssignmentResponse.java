package com.letslive.letslearnbackend.entities;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(name = "teacher_assignment_response")
public class TeacherAssignmentResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Store only the ID of the original assignment response
    @Column(name = "original_assignment_response_id", nullable = false)
    private UUID originalAssignmentResponseId;

    // Teacher's note about the assignment
    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    // Grade assigned by the teacher
    @Column(name = "mark", precision = 5, scale = 2)
    private BigDecimal mark;

    // Timestamp of when the response was graded
    @Column(name = "graded_at")
    private String gradedAt;

    // Teacher who graded the assignment
    @ManyToOne
    @JoinColumn(name = "graded_by")
    private User gradedBy;
}