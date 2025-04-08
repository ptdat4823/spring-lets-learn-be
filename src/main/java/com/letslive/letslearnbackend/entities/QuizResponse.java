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
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class QuizResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne()
    @JoinColumn(name = "student_id", referencedColumnName = "id")
    private User student;

    @Column(name = "topic_id")
    private UUID topicId;

    @Column(name = "status")
    private String status;

    @Column(name = "started_at")
    private String startedAt;

    @Column(name = "completed_at")
    private String completedAt;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuizResponseAnswer> answers;
}
