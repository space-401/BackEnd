package com.app.kkiri.domain.vo;

import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@Data
@NoArgsConstructor
public class MentionVO {
    private Long postId;
    private Long UserId;
}
