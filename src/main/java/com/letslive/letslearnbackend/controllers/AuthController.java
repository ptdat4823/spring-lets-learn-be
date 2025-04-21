package com.letslive.letslearnbackend.controllers;

import com.letslive.letslearnbackend.dto.AuthRequestDTO;
import com.letslive.letslearnbackend.dto.SignUpRequestDTO;
import com.letslive.letslearnbackend.dto.SuccessResponseDTO;
import com.letslive.letslearnbackend.security.SecurityUtils;
import com.letslive.letslearnbackend.services.AuthService;
import com.letslive.letslearnbackend.services.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/signup")
    public ResponseEntity<SuccessResponseDTO> registerUser(@RequestBody @Valid SignUpRequestDTO request) {
        authService.register(request);
        return ResponseEntity.ok(new SuccessResponseDTO("Successfully registered"));
    }

    @PostMapping("/login")
    public ResponseEntity<SuccessResponseDTO> loginUser(@RequestBody @Valid AuthRequestDTO request) {
        authService.login(request);
        return ResponseEntity.ok(new SuccessResponseDTO("Successfully logged in"));
    }

    @GetMapping("/refresh")
    public ResponseEntity<Void> refreshToken(HttpServletRequest request) {
        refreshTokenService.refreshToken(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        SecurityUtils.removeAllTokens(request);
        return ResponseEntity.noContent().build();
    }
}
