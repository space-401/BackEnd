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
    public List<CommentDTO> selectById(Long postId);
}
