package com.app.kkiri.security.model;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.app.kkiri.domain.dto.UserResponseDTO;
import com.app.kkiri.domain.vo.UserVO;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class AuthenticatedUser implements OAuth2User {
	private UserResponseDTO userResponseDTO;

	@Override
	public Map<String, Object> getAttributes() {
		return null;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return null;
	}

	@Override
	public String getName() {
		return userResponseDTO.getUserEmail();
	}
}
