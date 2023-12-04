package com.app.kkiri.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.app.kkiri.domain.vo.CommentVO;
import com.app.kkiri.mapper.CommentsMapper;

import lombok.RequiredArgsConstructor;

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

    // userId 와 startIndex 를 사용하여 사용자가 작성한 댓글을 조회
    public List<CommentVO> findByUserIdAndStartIndex(Long userId, Long startIndex) {
        return commentsMapper.selectByUserIdAndStartIndex(userId, startIndex);
    }

    // userId 를 사용하여 사용자가 작성한 댓글의 수를 조회
    public Long countByUserId(Long userId) {
        return commentsMapper.getTotalByUserId(userId);
    }
}
