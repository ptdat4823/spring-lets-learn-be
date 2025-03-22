
package com.letslive.letslearnbackend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class TopicAssigment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @Column(name = "topic_id")
    private UUID topicId;

    @JsonProperty("remindToGrade")
    private String remindToGrade;

    @JsonProperty("wordLimit")
    private Number wordLimit;

    @JsonProperty("maximumFile")
    private Number maximumFile;

    @JsonProperty("maximumFileSize")
    private Number maximumFileSize;
}