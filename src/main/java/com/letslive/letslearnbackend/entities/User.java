package com.letslive.letslearnbackend.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty
    private UUID id;

    @Column(unique = true, nullable = false)
    @JsonProperty
    @CreationTimestamp
    private String createdAt;
    
    @Column(length = 20, nullable = false)
    @JsonProperty
    private String username;
    
    @Column(unique = true, nullable = false)
    @JsonProperty
    private String email;

    @JsonIgnore
    private String passwordHash;

    @JsonProperty
    private String avatar;

    @JsonProperty
    @ColumnDefault("false")
    private Boolean isVerified;

    @JsonProperty
    private String role;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<EnrollmentDetail> enrollmentDetails = new ArrayList<>();
}