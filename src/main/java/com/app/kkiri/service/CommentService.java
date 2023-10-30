package com.app.kkiri.service;

import com.app.kkiri.domain.dto.CommentDTO;
import com.app.kkiri.domain.dto.PostDTO;
import com.app.kkiri.domain.dto.PostDetailDTO;
import com.app.kkiri.domain.vo.CommentVO;
import com.app.kkiri.repository.CommentsDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentsDAO commentsDAO;

    // 댓글 추가
    public void register(CommentVO commentVO){ commentsDAO.save(commentVO); }

    // 댓글 삭제
    public void remove(Long commentId){ commentsDAO.delete(commentId); }

    // 댓글 조회
    public List<CommentDTO> list(Long postId){ return commentsDAO.findById(postId); }
}
