package com.app.kkiri.domain.vo;

import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@Data
@NoArgsConstructor
public class PostVO {
    private Long postId;
    private String postTitle;
    private String postLocationKeyword;
    private String postLocationAddr;
    private Double postLocationLat;
    private Double postLocationLng;
    private String postBeginDate;
    private String postEndDate;
    private String postContent;
    private String postRegisterDate;
    private String postUpdateDate;
    private Long spaceId;
    private Long userId;

    public void create(Long postId, String postTitle, String postLocationKeyword, String postLocationAddr, Double postLocationLat, Double postLocationLng, String postBeginDate, String postEndDate, String postContent, String postRegisterDate, String postUpdateDate, Long spaceId, Long userId) {
        this.postId = postId;
        this.postTitle = postTitle;
        this.postLocationKeyword = postLocationKeyword;
        this.postLocationAddr = postLocationAddr;
        this.postLocationLat = postLocationLat;
        this.postLocationLng = postLocationLng;
        this.postBeginDate = postBeginDate;
        this.postEndDate = postEndDate;
        this.postContent = postContent;
        this.postRegisterDate = postRegisterDate;
        this.postUpdateDate = postUpdateDate;
        this.spaceId = spaceId;
        this.userId = userId;
    }
}
