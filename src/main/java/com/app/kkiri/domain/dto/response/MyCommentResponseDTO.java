package com.app.kkiri.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyCommentResponseDTO {

	private Long postId;
	private String postTitle;
	private String postContent;
	private String postCreateDate;
	private String spaceTitle;
	private String mainImgUrl;
	private Long commentId;
	private String commentContent;
	private String commentCreateDate;
}
