package com.letslive.letslearnbackend.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TopicMeeting {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JsonProperty("id")
    private UUID id;

    @JsonProperty("topicId")
    @Column(name = "topic_id")
    private UUID topicId;

    @JsonProperty("description")
    private String description;

    @JsonProperty("open")
    private String open;

    @JsonProperty("close")
    private String close;
}
