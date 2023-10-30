package com.app.kkiri.service;

import com.app.kkiri.domain.dto.PostDTO;
import com.app.kkiri.domain.dto.PostDetailDTO;
import com.app.kkiri.domain.vo.PostImgVO;
import com.app.kkiri.exceptions.CustomException;
import com.app.kkiri.exceptions.StatusCode;
import com.app.kkiri.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public PostDetailDTO postDetail(Long postId, Long userId){
        PostDetailDTO postDetailDTO = postsDAO.findById(postId);

        Long spaceId = postDetailDTO.getSpaceId();

        postDetailDTO.setImgsUrl(postImgsDAO.findById(postId));
        postDetailDTO.setTags(postTagsDAO.findById(postId));
        postDetailDTO.setSelectedUsers(mentionDAO.selectById(postId, spaceId));
        postDetailDTO.setUsers(spaceUsersDAO.findAll(spaceId, userId));
        postDetailDTO.setIsMine(postDetailDTO.getUserId() == userId);
        postDetailDTO.setIsBookmark(postBookmarksDAO.select(postId, userId) != 0);
//        postDetailDTO.setCommentConut();

        log.info("postDetail: " + postDetailDTO);

        return postDetailDTO;
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
