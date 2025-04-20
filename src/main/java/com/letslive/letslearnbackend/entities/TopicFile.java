package com.letslive.letslearnbackend.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TopicFile {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JsonProperty("id")
    private UUID id;

    @Column(nullable = false, name = "topic_id")
    @JsonProperty("topicId")
    private UUID topicId;

    @JsonProperty("description")
    private String description;

    @OneToOne(cascade = CascadeType.ALL)
    private CloudinaryFile file;
}
