package com.app.kkiri.domain.dto;

import com.app.kkiri.domain.vo.TagVO;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

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
