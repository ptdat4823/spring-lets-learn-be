package com.letslive.letslearnbackend.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.*;

@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "users")
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
    @JsonIgnore
    private List<User> students = new ArrayList<>();
}