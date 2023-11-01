package com.app.kkiri.mapper;

import com.app.kkiri.domain.dto.PostDTO;
import com.app.kkiri.domain.vo.PostVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PostsMapper {
    // 게시글 작성
    public Long insert(PostDTO postDTO);

    // 게시글 삭제
    public void delete(Long postId);

    // 게시글 수정
    public void update(PostDTO postDTO);

    // 게시글 상세 조회
    public PostVO selectById(Long postId);
}
