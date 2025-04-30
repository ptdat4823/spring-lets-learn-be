package com.letslive.letslearnbackend.repositories;

import com.letslive.letslearnbackend.entities.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
    Optional<Conversation> findByUser1IdAndUser2Id(UUID user1_id, UUID user2_id);
    List<Conversation> getConversationsByUser1IdOrUser2Id(UUID user1_id, UUID user2_id);
}