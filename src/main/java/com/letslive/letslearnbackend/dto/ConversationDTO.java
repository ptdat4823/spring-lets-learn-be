package com.letslive.letslearnbackend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ConversationDTO {
    private UUID id; // no need on upload
    private UserDTO user1;
    private UserDTO user2;
    private List<GetMessageDTO> messages; // no need on upload
}
