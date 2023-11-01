package com.app.kkiri.domain.dto;

import com.app.kkiri.domain.vo.SpaceUserVO;
import com.app.kkiri.domain.vo.UserVO;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@NoArgsConstructor
public class CommentDTO {
    private Long spaceId;
    private Long commentId;
    private Long commentGroup;
    private SpaceUserVO writer;
    private SpaceUserVO replyMember;
    private String commentContent;
    private String commentRegisterDate;
    private String commentRefYn;
    private Long userId;

    public void create(Long commentGroup, SpaceUserVO writer, SpaceUserVO replyMember, String commentContent, String commentRegisterDate, String commentRefYn) {
        this.commentGroup = commentGroup;
        this.writer = writer;
        this.replyMember = replyMember;
        this.commentContent = commentContent;
        this.commentRegisterDate = commentRegisterDate;
        this.commentRefYn = commentRefYn;
    }
}
