package com.letslive.letslearnbackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Topic {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;
    private String title;
    private String type;

    @Transient
    private Number studentCount;

    @Column(name = "section_id")
    private UUID sectionId;
}
