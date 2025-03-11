package com.letslive.letslearnbackend.services;

import com.letslive.letslearnbackend.dto.AuthRequestDTO;
import com.letslive.letslearnbackend.dto.SignUpRequestDTO;
import com.letslive.letslearnbackend.dto.UserDTO;
import com.letslive.letslearnbackend.entities.User;
import com.letslive.letslearnbackend.exception.CustomException;
import com.letslive.letslearnbackend.mappers.UserMapper;
import com.letslive.letslearnbackend.repositories.UserRepository;
import com.letslive.letslearnbackend.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    public User register(SignUpRequestDTO request) {
        UserDTO registerUserDTO = new UserDTO();
        registerUserDTO.setUsername(request.getUsername());
        registerUserDTO.setEmail(request.getEmail());
        registerUserDTO.setPassword(request.getPassword());
        registerUserDTO.setRole(request.getRole());

        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
                throw new CustomException("Email has been registered!", HttpStatus.BAD_REQUEST);
        });

        try {
            User registerUser = UserMapper.mapToEntity(registerUserDTO);
            registerUser.setPasswordHash(passwordEncoder.encode(registerUserDTO.getPassword()));
            User finalUser = userRepository.save(registerUser);
            refreshTokenService.createAndSetRefreshToken(registerUser);
            String accessToken = SecurityUtils.createAccessToken(finalUser.getId(), finalUser.getRole());
            SecurityUtils.setTokenToClient(accessToken, true);
            return finalUser;

        } catch (Exception e) {
            throw new CustomException("Failed to register: " + e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    public User login(AuthRequestDTO request) {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(request.getEmail());
        userDTO.setPassword(request.getPassword());

        User user = userRepository.findByEmail(userDTO.getEmail()).orElseThrow(() -> new CustomException("Email/Password is not correct!", HttpStatus.UNPROCESSABLE_ENTITY));
        refreshTokenService.createAndSetRefreshToken(user);
        String accessToken = SecurityUtils.createAccessToken(user.getId(), user.getRole());
        SecurityUtils.setTokenToClient(accessToken, true);

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new CustomException("Email/Password is not correct!", HttpStatus.UNAUTHORIZED);
        }

        return user;
    }
}