package com.app.kkiri.domain.vo;

import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@Data
@NoArgsConstructor
public class TagVO {
	private Long tagId;
	private String tagName;
	private Long spaceId;

	public void create(String tagName, Long spaceId) {
		this.tagName = tagName;
		this.spaceId = spaceId;
	}
}
