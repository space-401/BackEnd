package com.app.kkiri.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {
	private Long userId;
	private String socialType;
	private String userStatus;
	private String accessToken;
	private String refreshToken;
	private String userEmail;
}
