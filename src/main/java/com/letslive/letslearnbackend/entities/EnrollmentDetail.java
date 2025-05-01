package com.letslive.letslearnbackend.entities;

import com.letslive.letslearnbackend.utils.TimeUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
public class EnrollmentDetail {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    private LocalDateTime joinDate;

    public EnrollmentDetail() {
        this.joinDate = TimeUtils.getCurrentTimeGMT7(); // Automatically set the join date when created
    }
}
