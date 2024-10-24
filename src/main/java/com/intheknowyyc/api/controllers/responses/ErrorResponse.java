package com.intheknowyyc.api.controllers.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Represents a standardized error response for API.
 * This is typically used to encapsulate error details when an exception occurs.
 */
@Data
@Schema(description = "Standard error response for API exceptions.")
public class ErrorResponse {

    @Schema(description = "The timestamp when the error occurred", example = "2024-10-21T10:15:30")
    private LocalDateTime timestamp;

    @Schema(description = "HTTP status code", example = "400")
    private int status;

    @Schema(description = "A short description of the error", example = "Bad Request")
    private String error;

    @Schema(description = "Detailed message explaining the error", example = "Invalid request parameters")
    private String message;

    @Schema(description = "The path of the request that caused the error", example = "/api/events")
    private String path;

    /**
     * Constructs an ErrorResponse with the given parameters.
     *
     * @param status  the HTTP status code
     * @param error   a short description of the error
     * @param message a detailed message explaining the error
     * @param path    the path where the error occurred
     */
    public ErrorResponse(int status, String error, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }
}
