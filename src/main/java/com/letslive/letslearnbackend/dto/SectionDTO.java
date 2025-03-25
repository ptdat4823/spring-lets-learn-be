package com.letslive.letslearnbackend.dto;

import com.letslive.letslearnbackend.entities.Course;
import com.letslive.letslearnbackend.entities.Topic;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class SectionDTO {
    private UUID id;
    private int position;
    private String title;
    private String description;
    private UUID courseId;
    private List<TopicDTO> topics;
}
