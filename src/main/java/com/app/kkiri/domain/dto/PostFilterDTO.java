package com.app.kkiri.domain.dto;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@Data
@NoArgsConstructor
public class PostFilterDTO {
    private Long postId;
    private String postTitle;
    private String placeTitle;
    private List<TagDTO> selectedTags;
    private String postCreatedAt;
    private String postUpdatedAt;
    private List<SpaceUserRespnseDTO> usersList;
    private List<String> imgUrl;
    private PostPositionDTO position;
}
