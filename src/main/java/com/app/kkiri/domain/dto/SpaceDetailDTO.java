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
    private String spaceTitle;
    private String spaceDescription;
    private String imgUrl;
    private String spacePw;
    private int isAdmin;
    private int isFirst;
    private List<SpaceListDTO> userList;
    private List<TagVO> tagList;
}
