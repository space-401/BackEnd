package com.app.kkiri.service;

import com.app.kkiri.domain.dto.*;
import com.app.kkiri.domain.vo.PostImgVO;
import com.app.kkiri.domain.vo.PostVO;
import com.app.kkiri.domain.vo.SpaceUserVO;
import com.app.kkiri.exceptions.CustomException;
import com.app.kkiri.exceptions.StatusCode;
import com.app.kkiri.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostsDAO postsDAO;
    private final PostImgsDAO postImgsDAO;
    private final PostTagsDAO postTagsDAO;
    private final PostBookmarksDAO postBookmarksDAO;
    private final MentionDAO mentionDAO;
    private final SpaceUsersDAO  spaceUsersDAO;

    @Transactional(rollbackFor = Exception.class)
    // 게시글 작성
    public Long register(PostDTO postDTO, List<PostImgVO> imgs){
        postsDAO.save(postDTO);
        Long postId = postDTO.getPostId();
        List<Long> tags = postDTO.getTags();
        List<Long> people = postDTO.getPeople();

        log.info("postId: " + postId);

        for (PostImgVO img : imgs) {
            img.setPostId(postId);
            postImgsDAO.save(img);
        }

        for (Long tag: tags) {
            postTagsDAO.save(postId, tag);
        }

        for (Long person: people) {
            mentionDAO.insert(postId, person);
        }

        return postId;
    }

    // 게시글 삭제
    public void remove(Long postId){
        try {
            postsDAO.delete(postId);
        } catch (Exception e){
            throw new CustomException(StatusCode.BAD_REQUEST);
        }
    }

    // 게시글 수정
    @Transactional(rollbackFor = Exception.class)
    public void modify(PostDTO postDTO, List<PostImgVO> imgs){
        Long postId = postDTO.getPostId();
        List<Long> tags = postDTO.getTags();
        List<Long> people = postDTO.getPeople();

        postsDAO.set(postDTO);
        postImgsDAO.delete(postId);
        postTagsDAO.delete(postId);
        mentionDAO.delete(postId);

        for (PostImgVO img : imgs) {
            img.setPostId(postId);
            postImgsDAO.save(img);
        }

        for (Long tag: tags) {
            postTagsDAO.save(postId, tag);
        }

        for (Long person: people) {
            mentionDAO.insert(postId, person);
        }

    }

    // 게시글 상세조회
    public PostDetailResponseDTO postDetail(Long postId, Long userId){
        PostVO postVO = postsDAO.findById(postId);
        Long spaceId = postVO.getSpaceId();

        PostDetailResponseDTO postDetailResponseDTO = new PostDetailResponseDTO();
        postDetailResponseDTO.setSpaceId(spaceId);
        postDetailResponseDTO.setPostTitle(postVO.getPostTitle());
        postDetailResponseDTO.setPostDescription(postVO.getPostContent());
        postDetailResponseDTO.setPlaceTitle(postVO.getPostLocationKeyword());

        PostPositionDTO postPositionDTO = new PostPositionDTO();
        postPositionDTO.create(postVO.getPostLocationLat(), postVO.getPostLocationLng());
        postDetailResponseDTO.setPosition(postPositionDTO);
        postDetailResponseDTO.setPostCreatedAt(postVO.getPostRegisterDate());
        postDetailResponseDTO.setPostUpdatedAt(postVO.getPostUpdateDate());
        postDetailResponseDTO.setIsMine(postVO.getUserId() == userId);
        postDetailResponseDTO.setIsBookmark(postBookmarksDAO.select(postId, userId) != 0);

        PostDateDTO postDateDTO = new PostDateDTO();
        postDateDTO.create(postVO.getPostBeginDate(), postVO.getPostEndDate());
        postDetailResponseDTO.setDate(postDateDTO);

        postDetailResponseDTO.setImgsUrl(postImgsDAO.findById(postId));

        List<SpaceUserVO> selectedUsers = mentionDAO.selectById(postId, spaceId);
        List<SpaceUserRespnseDTO> selectedUserList = new ArrayList<>();
        for (SpaceUserVO user:selectedUsers) {
            SpaceUserRespnseDTO spaceUserRespnseDTO = new SpaceUserRespnseDTO();
            spaceUserRespnseDTO.setUserId(user.getUserId());
            spaceUserRespnseDTO.setUserName(user.getUserNickname());
            spaceUserRespnseDTO.setImgUrl(user.getProfileImgPath());

            selectedUserList.add(spaceUserRespnseDTO);
        }

        postDetailResponseDTO.setSelectedUsers(selectedUserList);

        List<SpaceUserVO> users = spaceUsersDAO.findAll(spaceId, userId);
        List<SpaceUserRespnseDTO> userList = new ArrayList<>();
        for (SpaceUserVO user:users) {
            SpaceUserRespnseDTO spaceUserRespnseDTO = new SpaceUserRespnseDTO();
            spaceUserRespnseDTO.setUserId(user.getUserId());
            spaceUserRespnseDTO.setUserName(user.getUserNickname());
            spaceUserRespnseDTO.setImgUrl(user.getProfileImgPath());

            userList.add(spaceUserRespnseDTO);
        }
        postDetailResponseDTO.setUserList(userList);
        postDetailResponseDTO.setSelectedTags(postTagsDAO.findById(postId));
//        postDetailDTO.setCommentConut();

        log.info("postDetailResponseDTO: " + postDetailResponseDTO);

        return postDetailResponseDTO;
    }

    // 게시글 북마크
    public void bookmark(Long postId, Long userId){
        try {
            if(postBookmarksDAO.select(postId, userId) == 0){
                postBookmarksDAO.setBookmark(postId, userId);
            } else{
                postBookmarksDAO.delete(postId, userId);
            }
        } catch (Exception e){
            throw new CustomException(StatusCode.BAD_REQUEST);
        }
    }
}
