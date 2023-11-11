package com.app.kkiri.domain.vo;

import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@Data
@NoArgsConstructor
public class SpaceUserVO {
	private Long spaceId;
	private Long userId;
	private boolean userAdminYn;
	private String userNickname;
	private String profileImgName;
	private String profileImgPath;
	private String profileImgUuid;
	private Long profileImgSize;
	private String spaceSignupDate;

	public void create(Long spaceId, Long userId, boolean userAdminYn, String userNickname, String profileImgName,
		String profileImgPath, String profileImgUuid, Long profileImgSize) {
		this.spaceId = spaceId;
		this.userId = userId;
		this.userAdminYn = userAdminYn;
		this.userNickname = userNickname;
		this.profileImgName = profileImgName;
		this.profileImgPath = profileImgPath;
		this.profileImgUuid = profileImgUuid;
		this.profileImgSize = profileImgSize;
	}

	public void createAdmin(Long userId) {
		this.userId = userId;
		this.userAdminYn = true;
		this.userNickname = "default";
		this.profileImgName = "default";
		this.profileImgPath = "default";
		this.profileImgUuid = "default";
		this.profileImgSize = 0L;
	}

	public void createNormal(Long userId) {
		this.userId = userId;
		this.userAdminYn = false;
		this.userNickname = "default";
		this.profileImgName = "default";
		this.profileImgPath = "default";
		this.profileImgUuid = "default";
		this.profileImgSize = 0L;
	}
}
