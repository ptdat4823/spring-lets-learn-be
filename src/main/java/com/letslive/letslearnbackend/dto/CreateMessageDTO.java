package com.letslive.letslearnbackend.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CreateMessageDTO {
    private UUID conversationId;
    private UUID senderId; // no need when upload
    private String content;
}
