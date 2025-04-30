package com.letslive.letslearnbackend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 1000)
    private String text;

    @ManyToOne(optional = false)
    private User user;

    @ManyToOne(optional = false)
    private Topic topic;

    private LocalDateTime createdAt = LocalDateTime.now();
}
