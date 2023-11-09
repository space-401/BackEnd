package com.app.kkiri.domain.vo;

import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@Data
@NoArgsConstructor
public class PostBookmarkVO {
    private Long postId;
    private Long UserId;
}
