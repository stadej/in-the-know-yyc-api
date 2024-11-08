package com.intheknowyyc.api.controllers;

import com.intheknowyyc.api.controllers.requests.CommentRequest;
import com.intheknowyyc.api.data.models.Comment;
import com.intheknowyyc.api.data.models.CommentStatus;
import com.intheknowyyc.api.data.models.TargetType;
import com.intheknowyyc.api.data.models.User;
import com.intheknowyyc.api.data.translators.CommentTranslator;
import com.intheknowyyc.api.services.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * REST controller for managing comments.
 */
@RestController
@RequestMapping("/comments")
@Tag(name = "Comment controller", description = "API for managing comments")
@SecurityRequirement(name = "swagger-auth")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * Endpoint for creating a new comment. Only authorized users can access this endpoint.
     *
     * @param comment the comment request data
     * @return the created Comment object wrapped in a ResponseEntity with status 201 (Created)
     */
    @Operation(summary = "Create a new comment",
            description = "Create a new comment. Only authorized users can create comments.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Comment created successfully", content = {@Content(schema = @Schema(implementation = Comment.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden access", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Comment> createComment(@RequestBody CommentRequest comment) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Comment createdComment = commentService.createComment(CommentTranslator.translateToComment(comment, (User) authentication.getPrincipal()));
        return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
    }

    /**
     * Retrieves comments for a specified event. If the requested status is not "APPROVED",
     * only admins are authorized to retrieve such comments.
     *
     * @param eventId the ID of the event
     * @param status  the status filter for the comments (optional, defaults to "APPROVED" if not provided)
     * @return the list of comments with the specified status, wrapped in a ResponseEntity with status 200 (OK)
     */
    @Operation(summary = "Retrieve comments for a specific event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of comments retrieved successfully", content = {@Content(schema = @Schema(implementation = Comment.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<Comment>> getCommentsForEvent(
            @Parameter(description = "ID of the event", example = "123")
            @PathVariable Long eventId,
            @Parameter(description = "Status filter for comments", example = "approved")
            @RequestParam(required = false) CommentStatus status) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication != null && authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));

        if (status == null) {
            status = CommentStatus.APPROVED;
        }
        if (status != CommentStatus.APPROVED && !isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can access non-approved comments");
        }

        List<Comment> comments = commentService.getCommentsForEvent(eventId, status);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    /**
     * Retrieves comments for a specified target type (SITE or EVENT). Non-admin users can only retrieve approved comments.
     *
     * @param targetType the target type for the comments
     * @param status     the status filter for the comments (optional, defaults to "APPROVED" if not provided)
     * @return the list of comments with the specified status, wrapped in a ResponseEntity with status 200 (OK)
     */
    @Operation(summary = "Retrieve comments for a specific target type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of comments retrieved successfully", content = {@Content(schema = @Schema(implementation = Comment.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/target/{targetType}")
    public ResponseEntity<List<Comment>> getCommentsForTarget(
            @Parameter(description = "Type of the target, e.g., site or event", example = "event")
            @PathVariable TargetType targetType,
            @Parameter(description = "Status filter for comments", example = "approved")
            @RequestParam(required = false) CommentStatus status) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication != null && authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));

        if (status == null) {
            status = CommentStatus.APPROVED;
        }
        if (status != CommentStatus.APPROVED && !isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can access non-approved comments");
        }

        List<Comment> comments = commentService.getCommentsForTarget(targetType, status);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    /**
     * Approves a comment. Only accessible to admin users.
     *
     * @param commentId the ID of the comment to approve
     * @return the approved Comment object wrapped in a ResponseEntity with status 200 (OK)
     */
    @Operation(summary = "Approve a comment (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment approved successfully", content = {@Content(schema = @Schema(implementation = Comment.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden access", content = @Content),
            @ApiResponse(responseCode = "404", description = "Comment not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)

    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{commentId}/approve")
    public ResponseEntity<Comment> approveComment(
            @Parameter(description = "ID of the comment to approve", example = "456")
            @PathVariable Long commentId) {
        Comment approvedComment = commentService.approveComment(commentId);
        return new ResponseEntity<>(approvedComment, HttpStatus.OK);
    }

    /**
     * Rejects a comment. Only accessible to admin users.
     *
     * @param commentId the ID of the comment to reject
     * @return the rejected Comment object wrapped in a ResponseEntity with status 200 (OK)
     */
    @Operation(summary = "Reject a comment (Admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment rejected successfully", content = {@Content(schema = @Schema(implementation = Comment.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden access", content = @Content),
            @ApiResponse(responseCode = "404", description = "Comment not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{commentId}/reject")
    public ResponseEntity<Comment> rejectComment(
            @Parameter(description = "ID of the comment to reject", example = "456")
            @PathVariable Long commentId) {
        Comment rejectedComment = commentService.rejectComment(commentId);
        return new ResponseEntity<>(rejectedComment, HttpStatus.OK);
    }

    /**
     * Retrieves replies for a specific comment. Only approved replies are returned.
     *
     * @param commentId the ID of the parent comment
     * @return the list of approved replies to the comment, wrapped in a ResponseEntity with status 200 (OK)
     */
    @Operation(summary = "Retrieve replies for a specific comment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of replies retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/{commentId}/replies")
    public ResponseEntity<List<Comment>> getRepliesForComment(
            @Parameter(description = "ID of the parent comment", example = "456")
            @PathVariable Long commentId) {
        List<Comment> replies = commentService.getRepliesForComment(commentId, CommentStatus.APPROVED);
        return new ResponseEntity<>(replies, HttpStatus.OK);
    }
}
