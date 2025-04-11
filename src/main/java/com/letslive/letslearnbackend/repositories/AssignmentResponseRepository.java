package com.letslive.letslearnbackend.repositories;

import com.letslive.letslearnbackend.entities.AssignmentResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AssignmentResponseRepository extends JpaRepository<AssignmentResponse, UUID> {
    List<AssignmentResponse> findAllByTopicId(UUID topicId);
    List<AssignmentResponse> findAllByStudentId(UUID studentId);
}
