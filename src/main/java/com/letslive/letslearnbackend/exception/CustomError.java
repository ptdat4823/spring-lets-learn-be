package com.letslive.letslearnbackend.exception;

import lombok.Data;

import java.util.Date;

@Data
public class CustomError {
    private final Date timestamp;
    private final String message;
    private final String details;
}