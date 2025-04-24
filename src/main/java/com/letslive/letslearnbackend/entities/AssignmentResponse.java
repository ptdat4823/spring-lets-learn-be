package com.letslive.letslearnbackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "assignment_response")
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
    private Double mark;

    @Column(name = "graded_at")
    private String gradedAt;

    @ManyToOne
    @JoinColumn(name = "graded_by")
    private User gradedBy;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "assignment_response_files",
            joinColumns = @JoinColumn(
                    name = "assignment_response_id",
                    table = "assignment_response",
                    referencedColumnName = "id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "cloudinary_file_id",
                    referencedColumnName = "id"
            )
    )
    private List<CloudinaryFile> cloudinaryFiles;
}
