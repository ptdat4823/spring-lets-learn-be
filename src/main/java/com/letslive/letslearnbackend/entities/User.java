package com.letslive.letslearnbackend.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "courses")
public class User {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @Column(unique = true, nullable = false)
    @CreationTimestamp
    private String createdAt;
    
    @Column(length = 20, nullable = false)
    private String username;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    private String passwordHash;

    private String avatar;

    @ColumnDefault("false")
    private Boolean isVerified;
    private String role;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<EnrollmentDetail> enrollmentDetails = new ArrayList<>();
}