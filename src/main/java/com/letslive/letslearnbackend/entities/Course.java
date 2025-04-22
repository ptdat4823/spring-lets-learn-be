package com.letslive.letslearnbackend.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    @ColumnDefault(value = "0")
    private Integer totalJoined; // not as same as currentJoin
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

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<EnrollmentDetail> enrollments = new ArrayList<>();
}