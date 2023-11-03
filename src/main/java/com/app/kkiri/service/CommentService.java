package com.app.kkiri.service;

import com.app.kkiri.domain.dto.CommentResponseDTO;
import com.app.kkiri.domain.dto.SpaceUserRespnseDTO;
import com.app.kkiri.domain.vo.CommentVO;
import com.app.kkiri.domain.vo.SpaceUserVO;
import com.app.kkiri.exceptions.CustomException;
import com.app.kkiri.exceptions.StatusCode;
import com.app.kkiri.repository.CommentsDAO;
import com.app.kkiri.repository.SpaceUsersDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final CommentsDAO commentsDAO;
    private final SpaceUsersDAO spaceUsersDAO;

    @Transactional(rollbackFor = Exception.class)
    // 댓글 추가
    public void register(CommentVO commentVO){
        try {
            if (commentVO.getCommentRefYn()) {
                commentsDAO.saveReply(commentVO);
            } else {
                commentsDAO.save(commentVO);
            }
        } catch (Exception e){
            throw new CustomException(StatusCode.BAD_REQUEST);
        }
    }

    // 댓글 삭제
    public void remove(Long commentId){
        try {
            commentsDAO.delete(commentId);
        } catch (Exception e){
            throw new CustomException(StatusCode.BAD_REQUEST);
        }
    }

    // 댓글 조회
    public List<CommentResponseDTO> list(Long postId, Long spaceId){
        List<CommentVO> commentVOList = commentsDAO.findById(postId);
        List<CommentResponseDTO> commentList = new ArrayList<>();

        for (CommentVO comment : commentVOList) {
            log.info("check");
            CommentResponseDTO commentResponseDTO = new CommentResponseDTO();

            commentResponseDTO.setId(comment.getCommentId());
            commentResponseDTO.setRefId(comment.getCommentGroup());

            SpaceUserVO userVO = spaceUsersDAO.findById(spaceId, comment.getUserId());
            SpaceUserRespnseDTO user = new SpaceUserRespnseDTO();
            user.setUserId(userVO.getUserId());
            user.setUserName(userVO.getUserNickname());
            user.setImgUrl(userVO.getProfileImgPath());

            commentResponseDTO.setWriter(user);

            userVO = spaceUsersDAO.findById(spaceId, commentsDAO.selectByGroup(comment.getCommentGroup()));
            user.setUserId(userVO.getUserId());
            user.setUserName(userVO.getUserNickname());
            user.setImgUrl(userVO.getProfileImgPath());
            commentResponseDTO.setReplyMember(user);

            commentResponseDTO.setContent(comment.getCommentContent());
            commentResponseDTO.setCreateDate(comment.getCommentRegisterDate());
            commentResponseDTO.setIsRef(comment.getCommentRefYn());

            commentList.add(commentResponseDTO);

        }

        return commentList;
    }
}
