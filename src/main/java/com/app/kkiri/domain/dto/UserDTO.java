package com.app.kkiri.domain.dto;

import com.app.kkiri.security.model.CustomOAuth2User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
	private Long userId;
	private String socialType;
	private String userStatus;
	private String accessToken;
	private String refreshToken;
	private String userEmail;
	private CustomOAuth2User customOAuth2User;
}
