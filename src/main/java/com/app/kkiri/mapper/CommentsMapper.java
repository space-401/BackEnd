package com.app.kkiri.mapper;

import com.app.kkiri.domain.dto.CommentDTO;
import com.app.kkiri.domain.vo.CommentVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

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
}
