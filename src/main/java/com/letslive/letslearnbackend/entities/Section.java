package com.letslive.letslearnbackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// Section.java
@Entity
@Table(name = "sections")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Section {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;
    private int position;
    private String title;
    private String description;

    @Column(name = "course_id")
    private UUID courseId;

    @OneToMany()
    @JoinColumn(name = "section_id", referencedColumnName = "id")
    private List<Topic> topics;
}