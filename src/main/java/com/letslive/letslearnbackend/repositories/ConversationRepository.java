package com.letslive.letslearnbackend.repositories;

import com.letslive.letslearnbackend.entities.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
    @Query("SELECT c FROM Conversation c WHERE (c.user1.id = :user1Id AND c.user2.id = :user2Id) OR (c.user1.id = :user2Id AND c.user2.id = :user1Id)")
    Optional<Conversation> findByUsers(UUID user1Id, UUID user2Id);

    @Query("SELECT c FROM Conversation c WHERE c.user1.id = :userId OR c.user2.id = :userId ORDER BY c.updatedAt DESC")
    List<Conversation> findAllByUserId(UUID userId);
}