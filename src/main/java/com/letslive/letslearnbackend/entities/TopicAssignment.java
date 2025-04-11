
package com.letslive.letslearnbackend.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
        import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(name = "topic_assignment")
public class TopicAssignment {
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

    @JsonProperty(value = "close", required = false)
    private String close;

    @JsonProperty("remindToGrade")
    private String remindToGrade;

    @JsonProperty("maximumFile")
    private Number maximumFile;

    @JsonProperty("maximumFileSize")
    private String maximumFileSize;

//    @JoinTable(
//            name = "topic_assignment_files",
//            joinColumns = @JoinColumn(name = "topic_assignment_id"),
//            inverseJoinColumns = @JoinColumn(name = "cloudinary_file_id")
//    )
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "topic_assignment_files",
            joinColumns = @JoinColumn(
                    name = "topic_assignment_id",
                    table = "topic_assignment",
                    referencedColumnName = "id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "cloudinary_file_id",
                    referencedColumnName = "id"
            )
    )
    @JsonProperty("cloudinaryFiles")
    private List<CloudinaryFile> cloudinaryFiles;
}