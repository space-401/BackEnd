package com.app.kkiri.repository;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.app.kkiri.domain.dto.PostDTO;
import com.app.kkiri.domain.vo.PostVO;
import com.app.kkiri.mapper.PostsMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PostsDAO {
    private final PostsMapper postsMapper;
    // 게시글 작성
    public Long save(PostDTO postDTO){ return postsMapper.insert(postDTO); }

    // 게시글 삭제
    public void delete(Long postId){ postsMapper.delete(postId); }

    // 게시글 수정
    public void set(PostDTO postDTO){ postsMapper.update(postDTO); }

    // 게시글 상세 조회
    public PostVO findById(Long postId){ return postsMapper.selectById(postId); }

    // 게시글 필터 조회
    public List<PostVO> findByFilter(Map<String, Object> param){ return postsMapper.selectByFilter(param); }

    // 필터된 게시글 총 개수
    public int getTotal(Map<String, Object> param){ return postsMapper.getTotal(param); }

    // userId 를 사용하여 post 를 삭제
    public void deleteByUserId(Long userId) {
        postsMapper.deleteByUserId(userId);
    }

    // 사용자가 북마크한 게시글 정보를 조회
    public List<PostVO> findBookmarkedPostsByUserIdAndPage(Long userId, Long startIndex) {
        return postsMapper.selectBookmarkedPosts(userId, startIndex);
    }

    // userId 를 사용하여 사용자가 작성한 게시글 조회
    public List<PostVO> findByUserId(Long userId, Long startIndex) {
        return postsMapper.selectByUserId(userId, startIndex);
    }

    // userId 를 사용하여 사용자가 작성한 게시글 수를 조회
    public Long countByUserId(Long userId) {
        return postsMapper.getTotalByUserId(userId);
    }
}
