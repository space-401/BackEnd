package com.app.kkiri.domain.dto;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@Data
@NoArgsConstructor
public class SpaceResponseDTO {
    private Long spaceId;
    private String spaceTitle;
    private String imgUrl;
    private List<SpaceUserRespnseDTO> userList;

    public void create(Long spaceId, String spaceTitle, String imgUrl, List<SpaceUserRespnseDTO> userList) {
        this.spaceId = spaceId;
        this.spaceTitle = spaceTitle;
        this.imgUrl = imgUrl;
        this.userList = userList;
    }
}
