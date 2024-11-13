package com.intheknowyyc.api.controllers.requests;

import com.intheknowyyc.api.data.models.TargetType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentRequest {

    @NotBlank(message = "Content must not be empty")
    @Schema(description = "Content of the comment", example = "This is a great event!")
    private String content;

    @Schema(description = "Name of the author if user is not registered", example = "John Doe")
    private String authorName;

    @NotNull(message = "Target type must be specified")
    @Schema(description = "Type of the comment target, e.g., site or event", example = "event")
    private TargetType targetType;

    @Schema(description = "Event ID if the comment is for a specific event", example = "123")
    private Long eventId;

    @Schema(description = "Parent comment ID for nested comments", example = "456")
    private Long parentCommentId;
}
