package com.letslive.letslearnbackend.controllers;

import com.letslive.letslearnbackend.dto.CreateMessageDTO;
import com.letslive.letslearnbackend.dto.GetMessageDTO;
import com.letslive.letslearnbackend.services.MessageService;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@AllArgsConstructor
public class ChatController {
    private final MessageService messageService;

    @MessageMapping("/sendMessage")
    @SendTo("/topic/conversation/{conversationId}")
    public GetMessageDTO sendMessage(@DestinationVariable UUID conversationId, CreateMessageDTO message) {
        return messageService.saveMessage(conversationId, message);
    }
}
