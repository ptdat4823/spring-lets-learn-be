package com.letslive.letslearnbackend.mappers;

import com.letslive.letslearnbackend.dto.UserDTO;
import com.letslive.letslearnbackend.entities.User;

public class UserMapper {
    public static User mapToEntity(UserDTO userDTO) {
        return User.builder()
                .id(userDTO.getId())
                .email(userDTO.getEmail())
                .passwordHash(userDTO.getPassword())
                .username(userDTO.getUsername())
                .role(userDTO.getRole())
                .build();
    }

    public static UserDTO mapToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }
}
