package com.app.kkiri.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.app.kkiri.domain.dto.PostDTO;
import com.app.kkiri.domain.vo.PostVO;
import com.app.kkiri.domain.vo.SpaceVO;

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

    // 게시글 필터 조회
    public List<PostVO> selectByFilter(Map<String, Object> param);

    // 필터된 게시글 총 개수
    public int getTotal(Map<String, Object> param);

    // userId 를 사용하여 post 를 삭제
    public void deleteByUserId(Long userId);

    // 사용자가 북마크한 게시글 정보를 조회
    public List<PostVO> selectBookmarkedPosts(Long userId, Long startIndex);

    // userId 를 사용하여 사용자가 작성한 게시글 조회
    public List<PostVO> selectByUserId(Long userId, Long startIndex);

    // userId 를 사용하여 사용자가 작성한 게시글 수를 조회
    public Long getTotalByUserId(Long userId);
}
