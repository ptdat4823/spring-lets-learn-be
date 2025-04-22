package com.letslive.letslearnbackend.repositories;

import com.letslive.letslearnbackend.entities.EnrollmentDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface EnrollmentDetailRepository extends JpaRepository<EnrollmentDetail, UUID> {
    boolean existsByStudentIdAndCourseId(UUID studentId, UUID courseId);
}
