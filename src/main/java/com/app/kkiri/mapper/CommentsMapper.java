package com.app.kkiri.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.app.kkiri.domain.vo.CommentVO;

@Mapper
public interface CommentsMapper {
    // 댓글 추가
    public void insert(CommentVO commentVO);

    // 대댓글 추가
    public void insertReply(CommentVO commentVO);

    // 댓글 삭제
    public void delete(Long commentId);

    // 댓글 조회
    public List<CommentVO> selectById(Long postId);

    // 댓글 번호가 GROUP인 userId
    public Long selectByGroup(Long commentGroup);

    // 게시글 댓글 총 개수
    public Long getTotal(Long postId);

    // userId 와 startIndex 를 사용하여 사용자가 작성한 댓글을 조회
    public List<CommentVO> selectByUserIdAndStartIndex(Long userId, Long startIndex);

    // userId 를 사용하여 사용자가 작성한 댓글의 수를 조회
    public Long getTotalByUserId(Long userId);
}
