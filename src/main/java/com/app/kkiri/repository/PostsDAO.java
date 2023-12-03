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
    public List<PostVO> findByfilter(Map<String, Object> param){ return postsMapper.selectByfilter(param); }

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

    // 사용자가 북마크한 게시글의 수를 조회
    public Long countBookmarkedPostsByUserId (Long userId) {
        return postsMapper.getTotalBookmarkedPosts(userId);
    }
}
