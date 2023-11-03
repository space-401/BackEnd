package com.app.kkiri.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserVO {

	private Long userId;
	private String socialType;
	private String userStatus;
	private String accessToken;
	private String refreshToken;
	private String userEmail;
}
