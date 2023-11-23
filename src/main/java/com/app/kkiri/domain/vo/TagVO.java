package com.app.kkiri.domain.vo;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagVO {

	private Long tagId;
	private String tagName;
	private Long spaceId;

	public void create(String tagName, Long spaceId) {
		this.tagName = tagName;
		this.spaceId = spaceId;
	}
}
