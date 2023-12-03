package com.app.kkiri.domain.dto.response;

import java.util.List;

import com.app.kkiri.domain.vo.PostVO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostBookmarkResponseDTO {

	List<PostResponseDTO> bookMarkList;
	int page;
	Long total;
	int itemLength;
}
