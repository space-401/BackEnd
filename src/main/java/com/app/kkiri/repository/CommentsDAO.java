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

    // 대댓글 추가
    public void saveReply(CommentVO commentVO){ commentsMapper.insertReply(commentVO);}

    // 댓글 삭제
    public void delete(Long commentId){ commentsMapper.delete(commentId); }

    // 댓글 조회
    public List<CommentVO> findById(Long postId){ return commentsMapper.selectById(postId); }

    // 댓글 번호가 GROUP인 userId
    public Long selectByGroup(Long commentGroup){ return commentsMapper.selectByGroup(commentGroup); }

    // 게시글 댓글 총 개수
    public Long getTotal(Long postId){ return commentsMapper.getTotal(postId); }
}
