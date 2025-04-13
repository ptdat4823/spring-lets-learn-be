package com.letslive.letslearnbackend.repositories;

import com.letslive.letslearnbackend.entities.TopicAssignment;
import com.letslive.letslearnbackend.entities.TopicMeeting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TopicMeetingRepository extends JpaRepository<TopicMeeting, UUID> {
    TopicMeeting findByTopicId(UUID topicId);
}
