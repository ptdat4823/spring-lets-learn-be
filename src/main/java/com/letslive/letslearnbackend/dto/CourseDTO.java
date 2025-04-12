package com.letslive.letslearnbackend.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class CourseDTO {
    @org.hibernate.validator.constraints.UUID
    private UUID id;

    @NotEmpty(message = "Title cannot be empty")
    private String title;

    private String description;
    private String imageUrl;
    private BigDecimal price;
    private String category;
    private String level;
    private Boolean isPublished = false;
    private UserDTO creator;
    private List<SectionDTO> sections;
    private List<UserDTO> students;
}
