package com.letslive.letslearnbackend.repositories;

import com.letslive.letslearnbackend.entities.TopicAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TopicAssigmentRepository extends JpaRepository<TopicAssignment, UUID> {
    Optional<TopicAssignment> findByTopicId(UUID topicId);

//    @Query("SELECT t FROM TopicAssignment t " +
//            "WHERE t.topicId IN :topicIds " +
//            "AND ((t.open IS NULL AND :open <= t.close) " +
//            "OR (t.close IS NULL AND :close >= t.open) " +
//            "OR (t.open IS NOT NULL AND t.open <= :close AND t.close IS NOT NULL AND t.close >= :open))")
//    List<TopicAssignment> findByTopicsAndOpenClose(List<UUID> topicIds, LocalDateTime open, LocalDateTime close);

    @Query("SELECT t FROM TopicAssignment t " +
            "WHERE t.topicId IN :topicIds " +
            "AND ((t.open IS NULL AND :open <= t.close) " +
            "OR (t.close IS NULL AND :close >= t.open) " +
            "OR (t.open IS NOT NULL AND t.open <= :close AND t.close IS NOT NULL AND t.close >= :open))")
    List<TopicAssignment> findByTopicsAndOpenClose(List<UUID> topicIds, String open, String close);
}
