package com.letslive.letslearnbackend.repositories;

import com.letslive.letslearnbackend.entities.Section;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SectionRepository extends JpaRepository<Section, UUID> {
    //List<Section> getByCourseId(UUID courseId);
}
