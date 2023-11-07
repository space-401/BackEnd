package com.app.kkiri.domain.dto;

import com.app.kkiri.domain.vo.TagVO;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
@NoArgsConstructor
public class PostDetailResponseDTO {
    private Long spaceId;
    private Boolean isMine;
    private String postTitle;
    private String postDescription;
    private String placeTitle;
    private PostPositionDTO position;
    private String postCreatedAt;
    private String postUpdatedAt;
    private PostDateDTO date;
    private Long commentConut;
    private Boolean isBookmark;
    private List<SpaceUserRespnseDTO> userList;
    private List<SpaceUserRespnseDTO> selectedUsers;
    private List<TagVO> selectedTags;
    private List<String> imgsUrl;
}
