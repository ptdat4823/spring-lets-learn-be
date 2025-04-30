package com.letslive.letslearnbackend.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CommentDTO {
    private UUID id; // no upload
    private String text;
    private UserDTO user; // no upload
    private TopicDTO topic; // no upload
    private LocalDateTime createdAt; // no upload
}