package com.app.kkiri.repository;

import com.app.kkiri.domain.dto.CommentDTO;
import com.app.kkiri.domain.vo.CommentVO;
import com.app.kkiri.mapper.CommentsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentsDAO {
    private final CommentsMapper commentsMapper;

    // 댓글 추가
    public void save(CommentVO commentVO){ commentsMapper.insert(commentVO); }

    // 댓글 삭제
    public void delete(Long commentId){ commentsMapper.delete(commentId); }

    // 댓글 조회
    public List<CommentDTO> findById(Long postId){ return commentsMapper.selectById(postId); }
}
