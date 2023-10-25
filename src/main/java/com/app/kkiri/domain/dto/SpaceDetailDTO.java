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
public class SpaceDetailDTO {
    private String spaceName;
    private String spaceDescription;
    private String spaceIconPath;
    private String spacePw;
    private int isAdmin;
    private int isFirst;
    private List<SpaceUserVO> spaceUsers;
    private List<TagVO> tags;
}
