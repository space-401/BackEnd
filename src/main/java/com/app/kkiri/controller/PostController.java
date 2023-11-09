package com.app.kkiri.controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.app.kkiri.domain.dto.PostDTO;
import com.app.kkiri.domain.dto.PostDetailResponseDTO;
import com.app.kkiri.domain.vo.PostBookmarkVO;
import com.app.kkiri.domain.vo.PostImgVO;
import com.app.kkiri.exceptions.CustomException;
import com.app.kkiri.exceptions.StatusCode;
import com.app.kkiri.security.jwt.JwtTokenProvider;
import com.app.kkiri.service.FileService;
import com.app.kkiri.service.PostService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@RequestMapping("/post")
@Slf4j
public class PostController {

    private final PostService postService;
    private final JwtTokenProvider jwtTokenProvider;
    private final FileService fileService;
    private final Logger LOGGER = LoggerFactory.getLogger(PostController.class);

    @Value("${file.rootPath.post}")
    private String rootPath;

    private Long getUserId(HttpServletRequest request){
        try{
            String token = jwtTokenProvider.resolveToken(request);
            return  jwtTokenProvider.getUserId(token);
        } catch (Exception e){
            throw new CustomException(StatusCode.INVALID_TOKEN);
        }
    }

    // 게시글 작성
    @PostMapping(path = "", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> register(@RequestPart PostDTO postDTO, @RequestPart List<MultipartFile> imgs, HttpServletRequest request) throws IOException{

        Long userId = getUserId(request);
        postDTO.setUserId(userId);

        String uploadPath = getUploadPath(); // 2023/11/10
        File uploadFullPath = new File(rootPath, uploadPath); // upload/post/2023/11/10
        if(!uploadFullPath.exists()) {
            uploadFullPath.mkdirs();
        }

        List<PostImgVO> imgList = new ArrayList<>();

        for (MultipartFile multipartFile : imgs) {

            PostImgVO postImgVO = new PostImgVO();

            String uuid = UUID.randomUUID().toString();
            String originalFileName = multipartFile.getOriginalFilename();
            String uploadFileName = uuid.toString() + "_" + originalFileName;
            File fullPath = new File(uploadFullPath, uploadFileName); // upload/post/2023/11/10/uuid_post.jpg
            LOGGER.info("[register()] value fullPath : {}", fullPath);

            String uploadUrl = fileService.uploadFile(multipartFile, fullPath.toString());
            LOGGER.info("[register()] value uploadUrl : {}", uploadUrl);

            postImgVO.setPostImgName(originalFileName); // post.jpg
            postImgVO.setPostImgPath(fullPath.toString()); // upload/post/2023/11/10/uuid_post.jpg
            postImgVO.setPostImgUuid(uuid); // uuid
            postImgVO.setPostImgSize(multipartFile.getSize()); // ..byte

            imgList.add(postImgVO);
        }

        postService.register(postDTO, imgList);

        return new ResponseEntity<>(StatusCode.OK, HttpStatus.OK);
    }

    // 게시글 삭제
    @DeleteMapping("")
    public ResponseEntity<?> remove(@RequestParam Long postId){
        postService.remove(postId);
        return new ResponseEntity<>(StatusCode.OK, HttpStatus.OK);
    }

    // 사진 업로드 위치 (해당 이미지를 업로드한 년/월/일)
    private String getUploadPath(){
        return new SimpleDateFormat("yyyy/MM/dd").format(new Date());
    };

    // 게시글 수정
    @PatchMapping(path = "", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> modify(@RequestPart PostDTO postDTO, @RequestPart List<MultipartFile> imgs) throws IOException {

        String uploadPath = getUploadPath(); // 2023/11/10
        File uploadFullPath = new File(rootPath, uploadPath); // upload/post/2023/11/10
        if(!uploadFullPath.exists()) {
            uploadFullPath.mkdirs();
        }

        List<PostImgVO> imgList = new ArrayList<>();

        for (MultipartFile multipartFile : imgs) {

            PostImgVO postImgVO = new PostImgVO();

            String uuid = UUID.randomUUID().toString();
            String originalFileName = multipartFile.getOriginalFilename();
            String uploadFileName = uuid.toString() + "_" + originalFileName;
            File fullPath = new File(uploadFullPath, uploadFileName); // upload/post/2023/11/10/uuid_dog.jpg
            LOGGER.info("[modify()] value fullPath : {}", fullPath);

            String uploadUrl = fileService.uploadFile(multipartFile, fullPath.toString());
            LOGGER.info("[modify()] value uploadUrl : {}", uploadUrl);

            postImgVO.setPostImgName(originalFileName); // post.jpg
            postImgVO.setPostImgPath(fullPath.toString()); // upload/post/2023/11/10/uuid_post.jpg
            postImgVO.setPostImgUuid(uuid); // uuid
            postImgVO.setPostImgSize(multipartFile.getSize()); // ..byte

            imgList.add(postImgVO);
        }

        postService.modify(postDTO, imgList);

        return new ResponseEntity<>(StatusCode.OK, HttpStatus.OK);
    }

    // 게시글 상세 조회
    @GetMapping("")
    public ResponseEntity<?> postDetail(@RequestParam Long postId, HttpServletRequest request){
//        Long userId = getUserId(request);
        Long userId = 1L;
        PostDetailResponseDTO postDetailType = postService.postDetail(postId, userId);
        return ResponseEntity.ok().body(postDetailType);
    }

    // 게시글 북마크
    @PostMapping(path = "/bookmark")
    public ResponseEntity<?> bookmark(@RequestBody PostBookmarkVO postId, HttpServletRequest request){
        Long userId = getUserId(request);
        log.info("postId: " + postId.getPostId());
        postService.bookmark(postId.getPostId(), userId);
        return new ResponseEntity<>(StatusCode.OK, HttpStatus.OK);
    }

}
