package com.letslive.letslearnbackend.dto;

public class SuccessResponseDTO {
    private String message;

    public SuccessResponseDTO(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
