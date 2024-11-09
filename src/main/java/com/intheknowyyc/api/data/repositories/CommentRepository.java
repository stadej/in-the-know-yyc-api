package com.intheknowyyc.api.data.repositories;

import com.intheknowyyc.api.data.models.Comment;
import com.intheknowyyc.api.data.models.CommentStatus;
import com.intheknowyyc.api.data.models.TargetType;
import io.micrometer.common.lang.NonNullApi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@NonNullApi
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Retrieve comments for a specific event with a specific status
    List<Comment> findByEventIdAndStatus(Long eventId, CommentStatus status);

    // Retrieve comments for a specific target type (e.g., SITE) with a specific status
    List<Comment> findByTargetTypeAndStatus(TargetType targetType, CommentStatus status);

    // Find a specific comment by ID (used for approving or rejecting)
    Optional<Comment> findById(Long commentId);

    // Retrieve comments with a specific parent comment ID (for nested comments)
    List<Comment> findByParentCommentIdAndStatus(Long parentCommentId, CommentStatus status);

}
