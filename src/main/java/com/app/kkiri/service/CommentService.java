package com.app.kkiri.service;

import java.util.ArrayList;
import java.util.List;

import com.app.kkiri.security.jwt.JwtTokenProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.kkiri.domain.dto.response.CommentResponseDTO;
import com.app.kkiri.domain.dto.response.SpaceUserResponseDTO;
import com.app.kkiri.domain.vo.CommentVO;
import com.app.kkiri.domain.vo.SpaceUserVO;
import com.app.kkiri.global.exception.BadRequestException;
import com.app.kkiri.global.exception.ExceptionCode;
import com.app.kkiri.repository.CommentsDAO;
import com.app.kkiri.repository.SpaceUsersDAO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;


@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final CommentsDAO commentsDAO;
    private final SpaceUsersDAO spaceUsersDAO;
    private final FileService fileService;

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
            throw new BadRequestException(ExceptionCode.INVALID_REQUEST);
        }
    }

    // 댓글 삭제
    public void remove(Long commentId){
        try {
            commentsDAO.delete(commentId);
        } catch (Exception e){
            // throw new CustomException(StatusCode.BAD_REQUEST);
        }
    }

    // 댓글 조회
    public List<CommentResponseDTO> list(Long postId, Long spaceId, Long userId){
        List<CommentVO> commentVOList = commentsDAO.findById(postId);
        List<CommentResponseDTO> commentList = new ArrayList<>();

        for (CommentVO comment : commentVOList) {
            log.info("check");
            CommentResponseDTO commentResponseDTO = new CommentResponseDTO();

            commentResponseDTO.setId(comment.getCommentId());
            commentResponseDTO.setRefId(comment.getCommentGroup());

            SpaceUserVO userVO = spaceUsersDAO.findById(spaceId, comment.getUserId());
            SpaceUserResponseDTO user = new SpaceUserResponseDTO();
            user.setUserId(userVO.getUserId());
            user.setUserName(userVO.getUserNickname());
            user.setImgUrl(userVO.getProfileImgPath());

            commentResponseDTO.setWriter(user);

            userVO = spaceUsersDAO.findById(spaceId, commentsDAO.selectByGroup(comment.getCommentGroup()));
            user.setUserId(userVO.getUserId());
            user.setUserName(userVO.getUserNickname());
            user.setImgUrl(fileService.getFileUrl(userVO.getProfileImgPath()));
            commentResponseDTO.setReplyMember(user);

            commentResponseDTO.setContent(comment.getCommentContent());
            commentResponseDTO.setCreateDate(comment.getCommentRegisterDate());
            commentResponseDTO.setIsRef(comment.getCommentRefYn());
            commentResponseDTO.setIsMyComment(comment.getUserId() == userId);

            commentList.add(commentResponseDTO);

        }

        return commentList;
    }

}
