package com.letslive.letslearnbackend.services;

import com.letslive.letslearnbackend.entities.Conversation;
import com.letslive.letslearnbackend.entities.User;
import com.letslive.letslearnbackend.exception.CustomException;
import com.letslive.letslearnbackend.repositories.ConversationRepository;
import com.letslive.letslearnbackend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConversationService {
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;

    public Conversation getOrCreateConversation(UUID user1Id, UUID user2Id) {
        User user1 = userRepository.findById(user1Id).orElseThrow(() -> new CustomException("User not found", HttpStatus.BAD_REQUEST));
        User user2 = userRepository.findById(user1Id).orElseThrow(() -> new CustomException("User not found", HttpStatus.BAD_REQUEST));

        Optional<Conversation> conversation = conversationRepository.findByUser1IdAndUser2Id(user1Id, user2Id);
        return conversation.orElseGet(() -> {
            Conversation newConversation = new Conversation();
            newConversation.setUser1(user1);
            newConversation.setUser2(user2);
            return conversationRepository.save(newConversation);
        });
    }
}

