package com.app.kkiri.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponseDTO {

	Long postId;
	String postTitle;
	Long postCommentCount;
	String postCreatedAt;
	String postWriterName;
}
