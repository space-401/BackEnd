package com.app.kkiri.domain.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyPostResponseDTO {

	private Long spaceId;
	private Long postId;
	private String postTitle;
	private String postCreatedAt;
	private Long postCommentCount;
	private List<UserProfileResponseDTO> selectedUsers;
}
