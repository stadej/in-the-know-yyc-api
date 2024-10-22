package com.intheknowyyc.api.utils;

/**
 * A utility class that holds constant values used throughout the application.
 */
public class Constants {

    /**
     * A constant string message used when an event is not found by ID.
     * The %d placeholder is intended to be replaced with the event's ID.
     */
    public static final String USER_NOT_FOUND_BY_ID = "User with ID %d does not exist!";

    /**
     * A constant string message used when a user is not found by email.
     * The %s placeholder is intended to be replaced with the user's email.
     */
    public static final String USER_NOT_FOUND_BY_EMAIL = "User with %s email not found";

    /**
     * A constant string message used when an event is not found by ID.
     * The %d placeholder is intended to be replaced with the event's ID.
     */
    public static final String EVENT_NOT_FOUND_BY_ID = "Event with ID %d does not exist!";

    /**
     * A constant string message used when a subscription is not found by ID.
     * The %d placeholder is intended to be replaced with the subscription's ID.
     */
    public static final String SUBSCRIPTION_NOT_FOUND_BY_ID = "Subscription with ID %d does not exist!";

    /**
     * A constant string message used when a subscription is not found by email.
     * The %s placeholder is intended to be replaced with the subscription's email.
     */
    public static final String SUBSCRIPTION_NOT_FOUND_BY_EMAIL = "Subscription with email %s not found";

    /**
     * A constant string message used when a subscription is not found by UUID.
     * The %s placeholder is intended to be replaced with the subscription's UUID.
     */
    public static final String SUBSCRIPTION_NOT_FOUND_BY_UUID = "Subscription with UUID %s not found";

}