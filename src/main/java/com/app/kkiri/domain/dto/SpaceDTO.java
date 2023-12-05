package com.app.kkiri.domain.dto;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    private String[] tags;
 }
