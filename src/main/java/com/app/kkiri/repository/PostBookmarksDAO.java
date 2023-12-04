package com.app.kkiri.repository;

import org.springframework.stereotype.Repository;

import com.app.kkiri.mapper.PostBookmarksMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PostBookmarksDAO {
    private final PostBookmarksMapper postBookmarksMapper;
    // 게시글 북마크
    public void setBookmark(Long postId, Long userId){ postBookmarksMapper.updateBookmark(postId, userId); }

    // 게시글 북마크 삭제
    public void delete(Long postId, Long userId){ postBookmarksMapper.delete(postId, userId); }

    // 게시글 북마크 조회
    public int select(Long postId, Long userId){ return postBookmarksMapper.select(postId, userId); }

    // 북마크 전체 조회
    public void selectAll(Long userId){};

    // 사용자가 북마크한 게시글의 수를 조회
    public Long countByUserId (Long userId) {
        return postBookmarksMapper.getTotalBookmarkedPosts(userId);
    }
}
