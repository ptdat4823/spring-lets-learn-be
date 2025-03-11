package com.letslive.letslearnbackend.services;

import com.letslive.letslearnbackend.entities.RefreshToken;
import com.letslive.letslearnbackend.entities.User;
import com.letslive.letslearnbackend.exception.CustomException;
import com.letslive.letslearnbackend.repositories.RefreshTokenRepository;
import com.letslive.letslearnbackend.security.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken createAndSetRefreshToken(User user) {
        var refreshToken = SecurityUtils.createRefreshToken(user);
        SecurityUtils.setTokenToClient(refreshToken.getValue(), false);
        return refreshTokenRepository.save(refreshToken);
    }

    public void refreshToken(HttpServletRequest request) {
        RefreshToken token = validateToken(request);
        String accessToken = SecurityUtils.createAccessToken(token.getUser().getId(), token.getUser().getRole());
        SecurityUtils.setTokenToClient(accessToken, true);
    }

    private RefreshToken validateToken(HttpServletRequest request) {
        String token = SecurityUtils.getToken(request, false);

        RefreshToken refreshToken = refreshTokenRepository
                .findByValue(token)
                .orElseThrow(() -> new CustomException("Refresh token not found!", HttpStatus.NOT_FOUND));

        if (refreshToken.getExpiresAt().compareTo(LocalDateTime.now()) < 0) {
            throw new CustomException("Refresh token expired, please log in again.", HttpStatus.UNAUTHORIZED);
        }

        SecurityUtils.validate(refreshToken.getValue());

        return refreshToken;
    }

}
