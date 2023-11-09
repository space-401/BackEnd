package com.app.kkiri.security.oAuth2Login;

import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Attributes {

	private Map<String, Object> mainAttributes;
	private Map<String, Object> subAttributes;
	private Map<String, Object> otherAttributes;
}
