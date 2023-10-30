package com.app.kkiri.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PostBookmarksMapper {
    // 게시글 북마크
    public void updateBookmark(Long postId, Long userId);

    // 게시글 북마크 삭제
    public void delete(Long postId, Long userId);

    // 게시글 북마크 조회
    public int select(Long postId, Long userId);

    // 북마크 전체 조회
    public void selectAll(Long userId);
}
