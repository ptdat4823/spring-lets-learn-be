package com.letslive.letslearnbackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.*;

@Entity
@Table(name = "courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;
    
    private String title;
    private String description;
    private String imageUrl;
    private BigDecimal price;
    private String category;
    private String level;
    private Boolean isPublished = false;

    @ManyToOne()
    @JoinColumn(name = "creator_id", referencedColumnName = "id")
    private User creator;

    @OneToMany
    @JoinColumn(name = "course_id", referencedColumnName = "id")
    private List<Section> sections;

    @ManyToMany(mappedBy = "courses")
    private Set<User> students = new HashSet<>(); 
}