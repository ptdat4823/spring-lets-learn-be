package com.letslive.letslearnbackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
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
    private UUID creatorId;
}
