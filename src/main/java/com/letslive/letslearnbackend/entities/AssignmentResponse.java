package com.letslive.letslearnbackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String topicId;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "answer")
    private String answer;

    @OneToMany
    @JoinTable(
            name = "assignment_response_files",
            joinColumns = @JoinColumn(name = "assignment_response_id"),
            inverseJoinColumns = @JoinColumn(name = "cloudinary_file_id")
    )
    private List<CloudinaryFile> cloudinaryFiles;
}
