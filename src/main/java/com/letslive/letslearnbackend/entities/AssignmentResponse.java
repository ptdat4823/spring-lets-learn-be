package com.letslive.letslearnbackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AssignmentResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    @Column(name = "topic_id")
    private UUID topicId;

    @Column(name = "submitted_at")
    private String submittedAt;

    @Column(name = "note")
    private String note;

    @Column(name = "mark")
    private BigDecimal mark;

    @Column(name = "graded_at")
    private String gradedAt;

    @ManyToOne
    @JoinColumn(name = "graded_by")
    private User gradedBy;

    @OneToMany
    @JoinTable(
            name = "assignment_response_files",
            joinColumns = @JoinColumn(name = "assignment_response_id"),
            inverseJoinColumns = @JoinColumn(name = "cloudinary_file_id")
    )
    private List<CloudinaryFile> cloudinaryFiles;
}
