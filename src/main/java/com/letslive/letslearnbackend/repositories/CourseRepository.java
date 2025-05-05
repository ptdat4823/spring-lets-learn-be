package com.letslive.letslearnbackend.repositories;

import com.letslive.letslearnbackend.entities.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CourseRepository extends JpaRepository<Course, UUID> {
    List<Course> findByCreatorId(UUID userId);
    List<Course> findAllByIsPublishedTrue();
    boolean existsByTitle(String title);
}
