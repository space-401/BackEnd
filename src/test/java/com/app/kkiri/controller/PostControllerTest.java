package com.app.kkiri.controller;

import com.app.kkiri.domain.dto.PostDTO;
import com.app.kkiri.domain.vo.PostImgVO;
import com.app.kkiri.domain.vo.SpaceVO;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class PostControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }


    @Test
    void register() throws Exception {
        final String fileName = "KakaoTalk_Photo_2023-04-14-17-58-36"; //파일명
        final String contentType = "jpeg"; //파일타입
        final String filePath = "/Users/son/Documents/마루/"+fileName+"."+contentType; //파일경로

        log.info("filePath: " + filePath);
        FileInputStream fileInputStream = new FileInputStream(filePath);

        PostDTO postDTO = new PostDTO();
        List<Long> tags = new ArrayList<>();
        List<Long> people = new ArrayList<>();
        people.add(2L);
        people.add(3L);
        tags.add(62L);
        tags.add(63L);

        postDTO.create(46L,"serviceTest", "serviceTest", people, tags,  "카페",231.2, 232.3, "2023/10/30", "2023/11/05");
        postDTO.setUserId(1L);

        // Mock파일생성
        MockMultipartFile imgs = new MockMultipartFile(
                "imgs", //name
                fileName + "." + contentType, //originalFilename
                contentType,
                fileInputStream
        );

        Gson gson = new Gson();
        String json = gson.toJson(postDTO);

        MockMultipartFile postDTO1 = new MockMultipartFile(
                "postDTO", //name
                null, //originalFilename
                "application/json",
                json.getBytes()
        );

        log.info("json: " + json);

        mockMvc.perform(
                        MockMvcRequestBuilders.multipart("/post/")
                                .file(imgs)
                                .file(postDTO1)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .characterEncoding("UTF-8"))
//                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void remove() throws Exception {
        log.info("postDetail: " + mockMvc.perform(MockMvcRequestBuilders.delete("/post/")
                        .param("postId", "45"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print()));
    }

    @Test
    void modify() throws Exception {
        final String fileName = "KakaoTalk_Photo_2023-04-14-18-21-14 002"; //파일명
        final String fileName2 = "KakaoTalk_Photo_2023-04-14-18-21-15 006"; //파일명
        final String contentType = "jpeg"; //파일타입
        final String filePath = "/Users/son/Documents/마루/"+fileName+"."+contentType; //파일경로
        final String filePath2 = "/Users/son/Documents/마루/"+fileName2+"."+contentType; //파일경로

        log.info("filePath: " + filePath);
        FileInputStream fileInputStream = new FileInputStream(filePath);
        FileInputStream fileInputStream2 = new FileInputStream(filePath2);

        PostDTO postDTO = new PostDTO();
        List<Long> tags = new ArrayList<>();
        List<Long> people = new ArrayList<>();
        people.add(2L);
        tags.add(62L);

        postDTO.create(46L,"수정", "serviceTest", people, tags,  "카페",231.2, 232.3, "2023/10/30", "2023/11/05");
        postDTO.setUserId(1L);
        postDTO.setPostId(45L);

        // Mock파일생성
        MockMultipartFile imgs = new MockMultipartFile(
                "imgs", //name
                fileName + "." + contentType, //originalFilename
                contentType,
                fileInputStream
        );

        // Mock파일생성
        MockMultipartFile imgs2 = new MockMultipartFile(
                "imgs", //name
                fileName2 + "." + contentType, //originalFilename
                contentType,
                fileInputStream2
        );

        Gson gson = new Gson();
        String json = gson.toJson(postDTO);

        MockMultipartFile postDTO1 = new MockMultipartFile(
                "postDTO", //name
                null, //originalFilename
                "application/json",
                json.getBytes()
        );

        log.info("json: " + json);

        final MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/post/");

        builder.with(request -> {
            request.setMethod("PATCH");

            return request;
        });

        mockMvc.perform(
                        builder.file(imgs)
                                .file(imgs2)
                                .file(postDTO1)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .characterEncoding("UTF-8"))
//                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void postDetail() throws Exception{
        log.info("postDetail: " + mockMvc.perform(MockMvcRequestBuilders.get("/post/")
                        .param("postId", "44"))
//                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print()));
    }

    @Test
    void bookmark() throws Exception{
        Map<String, Long> data = new HashMap<>();
        data.put("postId", 44L);

        Gson gson = new Gson();
        String json = gson.toJson(data);

        log.info("postDetail: " + mockMvc.perform(MockMvcRequestBuilders.post("/post/bookmark")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print()));
    }
}