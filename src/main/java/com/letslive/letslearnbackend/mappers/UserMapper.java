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
                .avatar(userDTO.getAvatar())
                .build();
    }

    public static UserDTO mapToDTO(User user) {
        UserDTO.UserDTOBuilder builder = UserDTO.builder();
        builder
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .avatar(user.getAvatar())
                .role(user.getRole());

        if (user.getCourses() != null) {
            // avoid stackoverflow
            user.getCourses().forEach(course -> course.setStudents(null));
            builder.courses(user.getCourses().stream().map(CourseMapper::mapToDTO).toList());
        }

        return builder.build();
    }
}
