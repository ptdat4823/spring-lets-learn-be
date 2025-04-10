package com.letslive.letslearnbackend.repositories;

import com.letslive.letslearnbackend.entities.TeacherAssignmentResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TeacherAssignmentResponseRepository extends JpaRepository<TeacherAssignmentResponse, UUID> {
    Optional<TeacherAssignmentResponse> getByOriginalAssignmentResponseId(UUID originalAssignmentResponseId);
}
