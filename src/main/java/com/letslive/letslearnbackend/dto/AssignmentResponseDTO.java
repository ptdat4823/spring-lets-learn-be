package com.letslive.letslearnbackend.dto;

import com.letslive.letslearnbackend.entities.CloudinaryFile;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class AssignmentResponseDTO {
    private UUID id;
    private UUID topicId;
    private UserDTO student; // no need on upload
    private String submittedAt;

    private String note;
    private BigDecimal mark;
    private String gradedAt; // no need on upload
    private UserDTO gradedBy; // no need on upload
    private List<CloudinaryFile> cloudinaryFiles;
}