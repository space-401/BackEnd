package com.app.kkiri.domain.dto;

import java.util.List;

import org.springframework.stereotype.Component;

import com.app.kkiri.domain.dto.response.SpaceUserResponseDTO;

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
    private List<SpaceUserResponseDTO> usersList;
    private List<String> imgUrl;
    private PostPositionDTO position;
}
