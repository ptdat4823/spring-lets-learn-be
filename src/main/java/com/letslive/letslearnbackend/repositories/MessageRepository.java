package com.letslive.letslearnbackend.repositories;

import com.letslive.letslearnbackend.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findByConversationIdOrderByTimestamp(UUID conversationId);
}
