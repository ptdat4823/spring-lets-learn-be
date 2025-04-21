package com.letslive.letslearnbackend.dto;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class UpdateUserDTO {
    @Size(min = 6, message = "Username must be at least 6 characters")
    private String username;
    private String avatar;
}