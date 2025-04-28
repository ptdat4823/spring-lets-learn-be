package com.letslive.letslearnbackend.repositories;

import com.letslive.letslearnbackend.entities.TopicQuiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TopicQuizRepository extends JpaRepository<TopicQuiz, UUID> {
    Optional<TopicQuiz> findByTopicId(UUID topicId);
//
//    @Query("SELECT t FROM TopicQuiz t " +
//            "WHERE t.topicId IN :topicIds " +
//            "AND ((t.open IS NULL AND :open <= t.close) " +
//            "OR (t.close IS NULL AND :close >= t.open) " +
//            "OR (t.open IS NOT NULL AND t.open <= :close AND t.close IS NOT NULL AND t.close >= :open))")
//    List<TopicQuiz> findByTopicsAndOpenClose(
//            @Param("topicIds") Collection<UUID> topicIds,
//            @Param("open") LocalDateTime open,
//            @Param("close") LocalDateTime close
//    );


    @Query("SELECT t FROM TopicQuiz t " +
            "WHERE t.topicId IN :topicIds " +
            "AND ((t.open IS NULL AND :open <= t.close) " +
            "OR (t.close IS NULL AND :close >= t.open) " +
            "OR (t.open IS NOT NULL AND t.open <= :close AND t.close IS NOT NULL AND t.close >= :open))")
    List<TopicQuiz> findByTopicsAndOpenClose(
            @Param("topicIds") Collection<UUID> topicIds,
            @Param("open") String open,
            @Param("close") String close
    );
}