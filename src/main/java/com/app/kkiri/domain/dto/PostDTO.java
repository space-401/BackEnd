package com.app.kkiri.domain.dto;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@Data
@NoArgsConstructor
public class PostDTO {
    private Long spaceId;
    private Long userId;
    private Long postId;
    private String postTitle;
    private String postContent;
    private List<Long> people;
    private List<Long> tags;
    private String postLocationKeyword;
    private Double postLocationLng;
    private Double postLocationLat;
    private String postBeginDate;
    private String postEndDate;

    public void create(Long spaceId, String postTitle, String postContent, List<Long> people, List<Long> tags,String postLocationKeyword, Double postLocationLng, Double postLocationLat, String postBeginDate, String postEndDate) {
        this.spaceId = spaceId;
        this.postTitle = postTitle;
        this.postContent = postContent;
        this.people = people;
        this.tags = tags;
        this.postLocationKeyword = postLocationKeyword;
        this.postLocationLng = postLocationLng;
        this.postLocationLat = postLocationLat;
        this.postBeginDate = postBeginDate;
        this.postEndDate = postEndDate;
    }
}
