package com.app.kkiri.domain.dto;

import com.app.kkiri.domain.vo.SpaceUserVO;
import com.app.kkiri.domain.vo.TagVO;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
@NoArgsConstructor
public class PostDetailDTO {
    private Long spaceId;
    private Long userId;
    private String postTitle;
    private String postContent;
    private String postLocationKeyword;
    private Double postLocationLat;
    private Double postLocationLng;
    private String postRegisterDate;
    private String postUpdateDate;
    private String postBeginDate;
    private String postEndDate;
    private Long commentConut;
    private List<SpaceUserVO> users;
    private List<SpaceUserVO> selectedUsers;
    private List<TagVO> tags;
    private List<String> imgsUrl;
    private Boolean isMine;
    private Boolean isBookmark;

}
