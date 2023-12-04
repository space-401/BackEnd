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
public class MyCommentListResponseDTO {

	private List<MyCommentResponseDTO> myCommentList;
	private int page;
	private Long total;
	private int itemLength;
}
