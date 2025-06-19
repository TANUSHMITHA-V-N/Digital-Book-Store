package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST) // Use 400 Bad Request for stock issues
public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(String message) {
        super(message);
    }

    public InsufficientStockException(String message, Throwable cause) {
        super(message, cause);
    }
}
