package com.letslive.letslearnbackend.controllers;

import com.letslive.letslearnbackend.entities.Message;
import com.letslive.letslearnbackend.services.ConversationService;
import com.letslive.letslearnbackend.services.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user/conversation")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;
    private final ConversationService conversationService;

    @GetMapping("/{conversationId}")
    public List<Message> getMessages(@PathVariable UUID conversationId) {
        return messageService.getMessagesByConversation(conversationId);
    }

//    @PostMapping("/{conversation}")
//    public Message sendMessage(@RequestBody Message message) {
//        return messageService.saveMessage(message);
//    }
}
