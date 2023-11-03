package com.app.kkiri.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.app.kkiri.domain.dto.CommentDTO;
import com.app.kkiri.domain.vo.CommentVO;
import com.app.kkiri.repository.CommentsDAO;
import com.app.kkiri.repository.SpaceUsersDAO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentsDAO commentsDAO;
    private final SpaceUsersDAO spaceUsersDAO;

    // 댓글 추가
    public void register(CommentVO commentVO){
        if(commentVO.getCommentRefYn()){
            commentsDAO.saveReply(commentVO);
        }else {
            commentsDAO.save(commentVO);
        }
    }

    // 댓글 삭제
    public void remove(Long commentId){ commentsDAO.delete(commentId); }

    // 댓글 조회
    public List<CommentDTO> list(Long postId, Long spaceId){
        List<CommentDTO> commentList = new ArrayList<>();
        commentList = commentsDAO.findById(postId);

        for (CommentDTO comment : commentList) {
            comment.setWriter(spaceUsersDAO.findById(spaceId, comment.getUserId()));

            if(comment.getCommentGroup() != null){
                comment.setReplyMember(spaceUsersDAO.findById(spaceId, comment.getCommentGroup()));
            }
        }

        return commentList;
    }
}
