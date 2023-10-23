package com.app.kkiri.domain.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Data
@Component
@NoArgsConstructor
public class SpaceDTO {
    private String spaceName;
    private String spaceDescription;
    private String spacePw;
    private Long spaceId;
    private String spaceCode;
    private int spaceUserTally;
    private String spaceRegisterDate;
    private String spaceUpdateDate;
    private String spaceIconName;
    private String spaceIconPath;
    private String spaceIconUuid;
    private Long spaceIconSize;
    private Long defaultImg;
}
