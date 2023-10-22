package com.app.kkiri.controller;

import java.io.FileInputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.app.kkiri.domain.vo.SpaceUserDTO;
import com.app.kkiri.domain.vo.SpaceVO;
import com.app.kkiri.domain.vo.TagVO;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class SpaceControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void list() throws Exception {
        log.info("list: " + mockMvc.perform(MockMvcRequestBuilders.get("/space/list").contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
               .andDo(MockMvcResultHandlers.print()));
    }

    @Test
    void spaceDetail() throws Exception{
        log.info("spaceDetail: " + mockMvc.perform(MockMvcRequestBuilders.get("/space/")
                        .param("spaceId", "44"))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print()));
    }

    @Test
    void register() throws Exception{
        // /Users/son/Documents/마루/KakaoTalk_Photo_2023-04-14-17-58-36.jpeg
        final String fileName = "KakaoTalk_Photo_2023-04-14-17-58-36"; //파일명
        final String contentType = "jpeg"; //파일타입
        final String filePath = "/Users/son/Documents/마루/"+fileName+"."+contentType; //파일경로

        log.info("filePath: " + filePath);
        FileInputStream fileInputStream = new FileInputStream(filePath);

        // Mock파일생성
        MockMultipartFile image = new MockMultipartFile(
                "imgUrl", //name
                fileName + "." + contentType, //originalFilename
                contentType,
                fileInputStream
        );

        SpaceVO spaceVO = new SpaceVO();
        spaceVO.setSpaceName("단위테스트");
        spaceVO.setSpaceDescription("단위테스트중인 스페이스");
        spaceVO.setSpacePw("00000");

        Gson gson = new Gson();
        String json = gson.toJson(spaceVO);

        log.info("json: " + json);

        mockMvc.perform(
                MockMvcRequestBuilders.multipart("/space/")
                        .file(image)
                        .content(json)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void remove() throws Exception{
        log.info("remove: " + mockMvc.perform(MockMvcRequestBuilders.delete("/space/")
                        .param("spaceId", "61"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print()));
    }

    @Test
    void modify() {
    }

    @Test
    void filter() {
    }

    @Test
    void tagList() throws Exception {
        log.info("tagList: " + mockMvc.perform(MockMvcRequestBuilders.get("/space/tag")
                        .param("spaceId", "44"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print()));
    }

    @Test
    void addTag() throws Exception {
        TagVO tagVO = new TagVO();
        tagVO.setTagName("테스트");
        tagVO.setSpaceId(44L);

        Gson gson = new Gson();
        String json = gson.toJson(tagVO);

        log.info("tagDTO: " + tagVO);
        log.info("json: " + json);
        log.info("addTag: " + mockMvc.perform(MockMvcRequestBuilders.post("/space/tag")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print()));
    }


    @Test
    void removeTag() throws Exception{
        log.info("removeTag: " + mockMvc.perform(MockMvcRequestBuilders.delete("/space/tag")
                        .param("spaceId", "44")
                        .param("tagId", "43"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print()));
    }

    @Test
    void enterSpace() throws Exception {
        SpaceVO spaceVO = new SpaceVO();
        spaceVO.setSpaceCode("adba0d48-9407-4dd5-b800-bafd4e72c2ee");
        spaceVO.setSpacePw("00000");

        Gson gson = new Gson();
        String json = gson.toJson(spaceVO);

        log.info("spaceVO: " + spaceVO);
        log.info("json: " + json);
        log.info("enter: " + mockMvc.perform(MockMvcRequestBuilders.post("/space/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print()));
    }

    @Test
    void withdrawSpace() throws Exception{
        log.info("withdrawSpace: " + mockMvc.perform(MockMvcRequestBuilders.delete("/space/user")
                        .param("spaceId", "44")
                        .param("userId", "3"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print()));
    }

    @Test
    void modifyInfo() throws Exception{
       final String fileName = "testImage1"; //파일명
       final String contentType = "png"; //파일타입
       final String filePath = "src/test/resources/testImage/"+fileName+"."+contentType; //파일경로
       FileInputStream fileInputStream = new FileInputStream(filePath);

       //Mock파일생성
       MockMultipartFile image = new MockMultipartFile(
               "image", //name
               fileName + "." + contentType, //originalFilename
               contentType,
               fileInputStream
       );

       SpaceUserDTO spaceUserDTO = new SpaceUserDTO();
       spaceUserDTO.setSpaceId(44L);
       spaceUserDTO.setUserId(2L);
       spaceUserDTO.setAdmin(true);

       Gson gson = new Gson();
       String json = gson.toJson(spaceUserDTO);

       log.info("spaceUserDTO: " + spaceUserDTO);
       log.info("json: " + json);
       log.info("modifyInfo: " + mockMvc.perform(MockMvcRequestBuilders.multipart("/space/user")
                               .file(image)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(json))
//                .andExpect(MockMvcResultMatchers.status().isOk())
               .andDo(MockMvcResultHandlers.print()));
    }
}