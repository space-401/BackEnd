package com.app.kkiri.domain.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@NoArgsConstructor
public class CommentVO {
    private Long commentId;
    private String commentContent;
    private Long commentClass;
    private Long commentGroup;
    private String commentRegisterDate;
    private Long postId;
    private Long userId;

    public void create(Long commentId, String commentContent, Long commentClass, Long commentGroup, String commentRegisterDate, Long postId, Long userId) {
        this.commentId = commentId;
        this.commentContent = commentContent;
        this.commentClass = commentClass;
        this.commentGroup = commentGroup;
        this.commentRegisterDate = commentRegisterDate;
        this.postId = postId;
        this.userId = userId;
    }
}
