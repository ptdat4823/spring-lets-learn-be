package com.letslive.letslearnbackend.repositories;

import com.letslive.letslearnbackend.entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByValue(String refreshToken);
}
