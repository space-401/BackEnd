package com.app.kkiri.domain.vo;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostImgVO {
    private Long postImgId;
    private String postImgName;
    private String postImgPath;
    private String postImgUuid;
    private Long postImgSize;
    private Long postId;

    public void create( String postImgName, String postImgPath, String postImgUuid, Long postImgSize, Long postId) {
        this.postImgName = postImgName;
        this.postImgPath = postImgPath;
        this.postImgUuid = postImgUuid;
        this.postImgSize = postImgSize;
        this.postId = postId;
    }
}
