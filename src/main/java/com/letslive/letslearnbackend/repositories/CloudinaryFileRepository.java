package com.letslive.letslearnbackend.repositories;

import com.letslive.letslearnbackend.entities.CloudinaryFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CloudinaryFileRepository extends JpaRepository<CloudinaryFile, UUID> {
}
