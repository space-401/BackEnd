package com.app.kkiri.domain.dto;

import com.app.kkiri.domain.vo.SpaceUserVO;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
@NoArgsConstructor
public class SpaceListDTO {
    private Long spaceId;
    private String spaceName;
    private String spaceIconPath;
    private List<SpaceUserVO> spaceUsers;
}
