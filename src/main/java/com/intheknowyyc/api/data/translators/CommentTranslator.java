package com.intheknowyyc.api.data.translators;

import com.intheknowyyc.api.controllers.requests.CommentRequest;
import com.intheknowyyc.api.data.models.*;

public class CommentTranslator {

    private CommentTranslator() {
    }

    public static Comment translateToComment(CommentRequest commentRequest, User user) {
        Comment comment = new Comment();
        comment.setUser(user);
        if (user != null) {
            if (UserRole.ROLE_ADMIN == user.getRole()) {
                comment.setAuthorName(commentRequest.getAuthorName());
                comment.setStatus(CommentStatus.APPROVED);
            } else {
                comment.setAuthorName(user.getFullName());
                comment.setStatus(CommentStatus.PENDING);
            }
        }
        comment.setContent(commentRequest.getContent());
        comment.setTargetType(commentRequest.getTargetType());
        comment.setParentCommentId(commentRequest.getParentCommentId());
        if (TargetType.EVENT == comment.getTargetType()) {
            Event event = new Event();
            event.setId(commentRequest.getEventId());
            comment.setEvent(event);
            comment.setParentCommentId(comment.getParentCommentId());
        }
        return comment;
    }
}
