package com.app.kkiri.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

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
