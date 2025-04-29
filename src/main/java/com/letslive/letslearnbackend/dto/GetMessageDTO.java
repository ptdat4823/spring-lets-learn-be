package com.letslive.letslearnbackend.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class GetMessageDTO {
    private UUID id;
    private UserDTO sender;
    private String content;
    private LocalDateTime timestamp;
}