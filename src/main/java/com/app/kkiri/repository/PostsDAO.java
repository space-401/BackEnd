package com.app.kkiri.repository;

import com.app.kkiri.domain.dto.PostDTO;
import com.app.kkiri.domain.dto.PostDetailDTO;
import com.app.kkiri.domain.vo.PostVO;
import com.app.kkiri.mapper.PostsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostsDAO {
    private final PostsMapper postsMapper;
    // 게시글 작성
    public Long save(PostDTO postDTO){ return postsMapper.insert(postDTO); }

    // 게시글 삭제
    public void delete(Long postId){ postsMapper.delete(postId); }

    // 게시글 수정
    public void set(PostDTO postDTO){ postsMapper.update(postDTO); }

    // 게시글 상세 조회
    public PostDetailDTO findById(Long postId){ return postsMapper.selectById(postId); }
}
