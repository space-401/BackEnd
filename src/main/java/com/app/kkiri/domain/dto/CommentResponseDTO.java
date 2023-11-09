package com.app.kkiri.domain.dto;

import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@Data
@NoArgsConstructor
public class CommentResponseDTO {
    private Long id;
    private Long refId;
    private SpaceUserRespnseDTO writer;
    private SpaceUserRespnseDTO replyMember;
    private String content;
    private String createDate;
    private Boolean isRef;

    public void create(Long id, Long refId, SpaceUserRespnseDTO writer, SpaceUserRespnseDTO replyMember, String content, String createDate, Boolean isRef) {
        this.id = id;
        this.refId = refId;
        this.writer = writer;
        this.replyMember = replyMember;
        this.content = content;
        this.createDate = createDate;
        this.isRef = isRef;
    }
}
