package com.app.kkiri.repository;

import com.app.kkiri.mapper.PostBookmarksMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostBookmarksDAO {
    private final PostBookmarksMapper postBookmarksMapper;
    // 게시글 북마크
    public void setBookmark(Long postId, Long userId){ postBookmarksMapper.updateBookmark(postId, userId); };

    // 게시글 북마크 삭제
    public void delete(Long postId, Long userId){ postBookmarksMapper.delete(postId, userId); };

    // 게시글 북마크 조회
    public int select(Long postId, Long userId){ return postBookmarksMapper.select(postId, userId); };

    // 북마크 전체 조회
    public void selectAll(Long userId){};
}
