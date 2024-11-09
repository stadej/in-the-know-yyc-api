package com.intheknowyyc.api.services;

import com.intheknowyyc.api.data.exceptions.ResourceNotFoundException;
import com.intheknowyyc.api.data.models.Comment;
import com.intheknowyyc.api.data.models.CommentStatus;
import com.intheknowyyc.api.data.models.TargetType;
import com.intheknowyyc.api.data.repositories.CommentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CommentService {

    private final EventService eventService;
    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(EventService eventService, CommentRepository commentRepository) {
        this.eventService = eventService;
        this.commentRepository = commentRepository;
    }

    public Comment createComment(Comment comment) {
        if (comment.getEvent() != null) {
            comment.setEvent(eventService.getEventById(comment.getEvent().getId()));
        }
        return commentRepository.save(comment);
    }

    public List<Comment> getCommentsForEvent(Long eventId, CommentStatus status) {
        return commentRepository.findByEventIdAndStatus(eventId, status);
    }

    public List<Comment> getCommentsForTarget(TargetType targetType, CommentStatus status) {
        return commentRepository.findByTargetTypeAndStatus(targetType, status);
    }

    public Optional<Comment> getCommentById(Long commentId) {
        return commentRepository.findById(commentId);
    }

    public Comment approveComment(Long commentId) {
        Optional<Comment> commentOpt = commentRepository.findById(commentId);
        if (commentOpt.isPresent()) {
            Comment comment = commentOpt.get();
            comment.setStatus(CommentStatus.APPROVED);
            return commentRepository.save(comment);
        }
        throw new ResourceNotFoundException("Comment not found");
    }

    public Comment rejectComment(Long commentId) {
        Optional<Comment> commentOpt = commentRepository.findById(commentId);
        if (commentOpt.isPresent()) {
            Comment comment = commentOpt.get();
            comment.setStatus(CommentStatus.REJECTED);
            return commentRepository.save(comment);
        }
        throw new ResourceNotFoundException("Comment not found");
    }

    public List<Comment> getRepliesForComment(Long parentCommentId, CommentStatus approved) {
        return commentRepository.findByParentCommentIdAndStatus(parentCommentId, approved);
    }
}
