package com.letslive.letslearnbackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @Column(unique = true, nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @Column(length = 20, nullable = false)
    private String username;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    private String passwordHash;

    private String avatar;

    @ColumnDefault("false")
    private Boolean isVerified;
    private String role;
}