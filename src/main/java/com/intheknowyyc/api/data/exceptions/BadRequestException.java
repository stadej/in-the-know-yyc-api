package com.intheknowyyc.api.data.exceptions;

/**
 * Custom exception for handling bad requests in the application.
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
