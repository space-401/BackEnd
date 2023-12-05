package com.app.kkiri.domain.dto.response;

import java.util.List;

import org.springframework.stereotype.Component;

import com.app.kkiri.domain.dto.PostDateDTO;
import com.app.kkiri.domain.dto.PostPositionDTO;
import com.app.kkiri.domain.vo.TagVO;

import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Long commentCount;
    private Boolean isBookmark;
    private List<SpaceUserResponseDTO> userList;
    private List<SpaceUserResponseDTO> selectedUsers;
    private List<TagVO> selectedTags;
    private List<String> imgsUrl;
}
