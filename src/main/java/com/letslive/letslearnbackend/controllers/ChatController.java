package com.letslive.letslearnbackend.controllers;

import com.letslive.letslearnbackend.dto.CreateMessageDTO;
import com.letslive.letslearnbackend.dto.GetMessageDTO;
import com.letslive.letslearnbackend.services.MessageService;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class ChatController {
    private final MessageService messageService;

    @MessageMapping("/sendMessage/{conversationId}")
    @SendTo("/topic/conversation/{conversationId}")
    public GetMessageDTO sendMessage(@Payload CreateMessageDTO message) {
        return messageService.saveMessage(message.getConversationId(), message);
    }
}
