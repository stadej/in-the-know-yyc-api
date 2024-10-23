package com.intheknowyyc.api.data.exceptions;

/**
 * An exception thrown when a subscription is not found.
 */
public class SubscriptionNotFoundException extends RuntimeException {
    public SubscriptionNotFoundException(String message) {
        super(message);
    }
}
