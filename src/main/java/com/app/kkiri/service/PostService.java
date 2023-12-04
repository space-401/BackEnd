package com.app.kkiri.service;

import static com.app.kkiri.global.exception.ExceptionCode.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.kkiri.domain.dto.PostDTO;
import com.app.kkiri.domain.dto.PostDateDTO;
import com.app.kkiri.domain.dto.response.PostDetailResponseDTO;
import com.app.kkiri.domain.dto.PostFilterDTO;
import com.app.kkiri.domain.dto.response.PostFilterResponseDTO;
import com.app.kkiri.domain.dto.PostPositionDTO;
import com.app.kkiri.domain.dto.response.SpaceUserResponseDTO;
import com.app.kkiri.domain.dto.TagDTO;
import com.app.kkiri.domain.vo.PostImgVO;
import com.app.kkiri.domain.vo.PostVO;
import com.app.kkiri.domain.vo.SpaceUserVO;
import com.app.kkiri.domain.vo.TagVO;
import com.app.kkiri.global.exception.BadRequestException;
import com.app.kkiri.repository.CommentsDAO;
import com.app.kkiri.repository.MentionDAO;
import com.app.kkiri.repository.PostBookmarksDAO;
import com.app.kkiri.repository.PostImgsDAO;
import com.app.kkiri.repository.PostTagsDAO;
import com.app.kkiri.repository.PostsDAO;
import com.app.kkiri.repository.SpaceUsersDAO;
import com.app.kkiri.security.jwt.JwtTokenProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostsDAO postsDAO;
    private final PostImgsDAO postImgsDAO;
    private final PostTagsDAO postTagsDAO;
    private final PostBookmarksDAO postBookmarksDAO;
    private final MentionDAO mentionDAO;
    private final SpaceUsersDAO spaceUsersDAO;
    private final CommentsDAO commentsDAO;
    private final FileService fileService;
    private final JwtTokenProvider jwtTokenProvider;
    private final Logger LOGGER = LoggerFactory.getLogger(PostService.class);

    @Transactional(rollbackFor = Exception.class)
    // 게시글 작성
    public Long register(PostDTO postDTO, List<PostImgVO> imgs){

        postsDAO.save(postDTO);
        Long postId = postDTO.getPostId();
        LOGGER.info("[register()] postId : {}", postId);

        List<Long> tags = postDTO.getTags();
        List<Long> people = postDTO.getPeople();

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
            // throw new CustomException(StatusCode.BAD_REQUEST);
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

        List<String> fileKeys = postImgsDAO.findById(postId);
        List<String> fileURIs = new ArrayList<>();
        fileKeys.forEach(fileKey -> fileURIs.add(fileService.getFileUrl(fileKey)));
        postDetailResponseDTO.setImgsUrl(fileURIs);

        List<SpaceUserVO> selectedUsers = mentionDAO.selectById(postId, spaceId);
        List<SpaceUserResponseDTO> selectedUserList = new ArrayList<>();
        for (SpaceUserVO user:selectedUsers) {
            SpaceUserResponseDTO spaceUserResponseDTO = new SpaceUserResponseDTO();
            spaceUserResponseDTO.setUserId(user.getUserId());
            spaceUserResponseDTO.setUserName(user.getUserNickname());
            spaceUserResponseDTO.setImgUrl(fileService.getFileUrl(user.getProfileImgPath()));

            selectedUserList.add(spaceUserResponseDTO);
        }

        postDetailResponseDTO.setSelectedUsers(selectedUserList);

        List<SpaceUserVO> users = spaceUsersDAO.findAll(spaceId, userId);
        List<SpaceUserResponseDTO> userList = new ArrayList<>();
        for (SpaceUserVO user:users) {
            SpaceUserResponseDTO spaceUserResponseDTO = new SpaceUserResponseDTO();
            spaceUserResponseDTO.setUserId(user.getUserId());
            spaceUserResponseDTO.setUserName(user.getUserNickname());
            spaceUserResponseDTO.setImgUrl(fileService.getFileUrl(user.getProfileImgPath()));

            userList.add(spaceUserResponseDTO);
        }
        postDetailResponseDTO.setUserList(userList);

//        List<TagVO> tagVOList = postTagsDAO.findById(postId);
//        List<TagDTO> tagList = new ArrayList<>();
//
//        for (TagVO tag:tagVOList) {
//            TagDTO tagDTO = new TagDTO();
//            tagDTO.setTagId(tag.getTagId());
//            tagDTO.setTagTitle(tag.getTagName());
//
//            tagList.add(tagDTO);
//        }
//        postDetailResponseDTO.setSelectedTags(tagList);
        postDetailResponseDTO.setSelectedTags(postTagsDAO.findById(postId));
        postDetailResponseDTO.setCommentCount(commentsDAO.getTotal(postId));
        LOGGER.info("[postDetail()] postDetailResponseDTO : {}", postDetailResponseDTO);

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
            // throw new CustomException(StatusCode.BAD_REQUEST);
        }
    }

    // 게시글 필터 조회
    public PostFilterResponseDTO filter(Map<String, Object> param, Long userId){
        try{
            PostFilterResponseDTO postFilterResponseDTO = new PostFilterResponseDTO();
            List<PostVO> postVOListst = postsDAO.findByfilter(param);
            List<PostFilterDTO> postList = new ArrayList<>();

            for (PostVO post: postVOListst) {
                Long postId = post.getPostId();
                PostFilterDTO postFilterDTO = new PostFilterDTO();
                postFilterDTO.setPostId(post.getPostId());
                postFilterDTO.setPostTitle(post.getPostTitle());
                postFilterDTO.setPlaceTitle(post.getPostLocationKeyword());
                postFilterDTO.setPostCreatedAt(post.getPostRegisterDate());
                postFilterDTO.setPostUpdatedAt(post.getPostUpdateDate());

                PostPositionDTO postPositionDTO = new PostPositionDTO();
                postPositionDTO.create(post.getPostLocationLat(), post.getPostLocationLng());
                postFilterDTO.setPosition(postPositionDTO);

                List<String> fileKeys = postImgsDAO.findById(postId);
                List<String> fileURIs = new ArrayList<>();
                fileKeys.forEach(fileKey -> fileURIs.add(fileService.getFileUrl(fileKey)));
                postFilterDTO.setImgUrl(fileURIs);

                List<TagVO> tagVOList = postTagsDAO.findById(postId);
                List<TagDTO> tagList = new ArrayList<>();

                for (TagVO tag:tagVOList) {
                    TagDTO tagDTO = new TagDTO();
                    tagDTO.setTagId(tag.getTagId());
                    tagDTO.setTagName(tag.getTagName());

                    tagList.add(tagDTO);
                }

                postFilterDTO.setSelectedTags(tagList);

                List<SpaceUserVO> selectedUsers = mentionDAO.selectById(postId,(Long)param.get("spaceId"));
                List<SpaceUserResponseDTO> selectedUserList = new ArrayList<>();
                for (SpaceUserVO user:selectedUsers) {
                    SpaceUserResponseDTO spaceUserResponseDTO = new SpaceUserResponseDTO();
                    spaceUserResponseDTO.setUserId(user.getUserId());
                    spaceUserResponseDTO.setUserName(user.getUserNickname());
                    spaceUserResponseDTO.setImgUrl(fileService.getFileUrl(user.getProfileImgPath()));

                    selectedUserList.add(spaceUserResponseDTO);
                }
                postFilterDTO.setUsersList(selectedUserList);

                postList.add(postFilterDTO);
            }

            postFilterResponseDTO.setPostList(postList);
            LOGGER.info("[filter()] Amount : {}", param.get("amount"));
            postFilterResponseDTO.setItemLength((Integer)param.get("amount"));
            postFilterResponseDTO.setTotal(postsDAO.getTotal(param));
            LOGGER.info("[filter()] Page : {}", param.get("page"));
            postFilterResponseDTO.setPage((Integer)param.get("page"));
            LOGGER.info("[filter()] postFilterResponseDTO : {}", postFilterResponseDTO);

            return postFilterResponseDTO;
        } catch (Exception e) {
            throw new BadRequestException(INVALID_REQUEST);
        }
    }

    // userId 를 사용하여 post 를 삭제
    public void deleteByUserId(Long userId) {

        postsDAO.deleteByUserId(userId);
    }
}
