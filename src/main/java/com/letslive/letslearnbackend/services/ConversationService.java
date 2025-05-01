package com.letslive.letslearnbackend.services;

import com.letslive.letslearnbackend.dto.ConversationDTO;
import com.letslive.letslearnbackend.entities.Conversation;
import com.letslive.letslearnbackend.entities.User;
import com.letslive.letslearnbackend.exception.CustomException;
import com.letslive.letslearnbackend.mappers.MessageMapper;
import com.letslive.letslearnbackend.mappers.UserMapper;
import com.letslive.letslearnbackend.repositories.ConversationRepository;
import com.letslive.letslearnbackend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConversationService {
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;

    public ConversationDTO getOrCreateConversation(UUID user1Id, UUID user2Id) {
        User user1 = userRepository.findById(user1Id).orElseThrow(() -> new CustomException("User not found", HttpStatus.BAD_REQUEST));
        User user2 = userRepository.findById(user2Id).orElseThrow(() -> new CustomException("User not found", HttpStatus.BAD_REQUEST));

        Optional<Conversation> conversation = conversationRepository.findByUsers(user1Id, user2Id);
        Conversation createdConversation = conversation.orElseGet(() -> {
            Conversation newConversation = new Conversation();
            newConversation.setUser1(user1);
            newConversation.setUser2(user2);
            return conversationRepository.save(newConversation);
        });

        return ConversationDTO
                .builder()
                .id(createdConversation.getId())
                .messages(createdConversation.getMessages().stream().map(MessageMapper::mapToGetMessageDTO).toList())
                .user1(UserMapper.mapToDTO(user1))
                .user2(UserMapper.mapToDTO(user2))
                .build();
    }

    public List<ConversationDTO> getAllByUserId(UUID userId) {
        if (!userRepository.existsById(userId)) throw new CustomException("User not found", HttpStatus.BAD_REQUEST);
        List<Conversation> conversations = conversationRepository.findAllByUserId(userId);

        return conversations.stream().map(c -> ConversationDTO
                .builder()
                .id(c.getId())
                .messages(c.getMessages().stream().map(MessageMapper::mapToGetMessageDTO).toList())
                .user1(UserMapper.mapToDTO(c.getUser1()))
                .user2(UserMapper.mapToDTO(c.getUser2()))
                .build()
        ).toList();
    }
}

