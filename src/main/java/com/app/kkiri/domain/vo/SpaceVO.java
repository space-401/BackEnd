package com.app.kkiri.domain.vo;

import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@Data
@NoArgsConstructor
public class SpaceVO {
	private Long spaceId;
	private String spaceName;
	private String spaceDescription;
	private String spacePw;
	private String spaceCode;
	private int spaceUserTally;
	private String spaceRegisterDate;
	private String spaceUpdateDate;
	private String spaceIconName;
	private String spaceIconPath;
	private String spaceIconUuid;
	private Long spaceIconSize;

	public void create(String spaceName, String spaceDescription, String spacePw, String spaceCode,
		String spaceIconName, String spaceIconPath, String spaceIconUuid, Long spaceIconSize) {
		this.spaceName = spaceName;
		this.spaceDescription = spaceDescription;
		this.spacePw = spacePw;
		this.spaceCode = spaceCode;
		this.spaceIconName = spaceIconName;
		this.spaceIconPath = spaceIconPath;
		this.spaceIconUuid = spaceIconUuid;
		this.spaceIconSize = spaceIconSize;
	}
}
