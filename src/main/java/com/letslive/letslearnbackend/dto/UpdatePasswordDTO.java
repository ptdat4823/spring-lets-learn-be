package com.letslive.letslearnbackend.dto;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class UpdatePasswordDTO {
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String oldPassword;
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String newPassword;
}