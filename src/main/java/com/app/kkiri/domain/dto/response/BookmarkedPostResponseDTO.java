package com.app.kkiri.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookmarkedPostResponseDTO {

	private Long postId;
	private String postTitle;
	private Long postCommentCount;
	private String postCreatedAt;
	private String postWriterName;
}
