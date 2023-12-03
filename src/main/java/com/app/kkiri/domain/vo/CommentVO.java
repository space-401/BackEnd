package com.app.kkiri.domain.vo;

import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
public class CommentVO {
    private Long commentId;
    private String commentContent;
    private Boolean commentRefYn;
    private Long commentGroup;
    private String commentRegisterDate;
    private Long postId;
    private Long userId;

    public void create(String commentContent, Boolean commentRefYn, Long postId, Long userId) {
        this.commentContent = commentContent;
        this.commentRefYn = commentRefYn;
        this.postId = postId;
        this.userId = userId;
    }
    public void create(String commentContent, Boolean commentRefYn, Long commentGroup, Long postId, Long userId) {
        this.commentContent = commentContent;
        this.commentRefYn = commentRefYn;
        this.commentGroup = commentGroup;
        this.postId = postId;
        this.userId = userId;
    }
}
