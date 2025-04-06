
package com.letslive.letslearnbackend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class TopicAssigment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JsonProperty("id")
    private UUID id;

    @JsonProperty("topicId")
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

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
            name = "assignment_response_files",
            joinColumns = @JoinColumn(name = "assignment_response_id"),
            inverseJoinColumns = @JoinColumn(name = "cloudinary_file_id")
    )
    @JsonProperty("cloudinaryFiles")
    private List<CloudinaryFile> cloudinaryFiles;
}