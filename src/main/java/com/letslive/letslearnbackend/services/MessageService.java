package com.letslive.letslearnbackend.services;

import com.letslive.letslearnbackend.dto.CreateMessageDTO;
import com.letslive.letslearnbackend.dto.GetMessageDTO;
import com.letslive.letslearnbackend.entities.Conversation;
import com.letslive.letslearnbackend.entities.Message;
import com.letslive.letslearnbackend.entities.User;
import com.letslive.letslearnbackend.exception.CustomException;
import com.letslive.letslearnbackend.mappers.MessageMapper;
import com.letslive.letslearnbackend.repositories.ConversationRepository;
import com.letslive.letslearnbackend.repositories.MessageRepository;
import com.letslive.letslearnbackend.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;

    public List<Message> getMessagesByConversation(UUID conversationId) {
        return messageRepository.findByConversationIdOrderByTimestamp(conversationId);
    }

    public GetMessageDTO saveMessage(UUID conversationId, CreateMessageDTO message) {
        Conversation conversation = conversationRepository.findById(conversationId).orElseThrow(() -> new CustomException("Conversation not found", HttpStatus.NOT_FOUND));
        User sender = userRepository.findById(message.getSenderId()).orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        Message messageEntity = new Message();
        messageEntity.setContent(message.getContent());
        messageEntity.setConversation(conversation);
        messageEntity.setSender(sender);
        Message savedMessage = messageRepository.save(messageEntity);
        return MessageMapper.mapToGetMessageDTO(savedMessage);
    }
}
