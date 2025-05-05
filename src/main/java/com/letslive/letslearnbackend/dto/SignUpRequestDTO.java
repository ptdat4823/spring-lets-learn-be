
package com.letslive.letslearnbackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignUpRequestDTO {
    @Email(message = "Invalid email", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    @NotEmpty(message = "Email cannot be empty")
    private String email;

    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @NotEmpty(message = "Role must be either teacher or learner")
    private String role;

    @Size(min = 6, message = "Username must be at least 6 characters")
    @NotEmpty(message = "Username cannot be empty")
    private String username;
}