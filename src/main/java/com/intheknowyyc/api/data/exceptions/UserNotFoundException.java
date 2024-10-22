package com.intheknowyyc.api.data.exceptions;

/**
 * An exception thrown when a subscription is not found.
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
