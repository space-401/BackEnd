package com.app.kkiri.controller;

import com.app.kkiri.domain.dto.SpaceDTO;
import com.app.kkiri.domain.dto.SpaceUserDTO;
import com.app.kkiri.domain.vo.SpaceVO;
import com.app.kkiri.domain.vo.TagVO;
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
//                .andExpect(MockMvcResultMatchers.status().isOk())
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

        SpaceVO spaceVO = new SpaceVO();
        spaceVO.setSpaceName("단위테스트");
        spaceVO.setSpaceDescription("단위테스트중인 스페이스");
        spaceVO.setSpacePw("00000");

        // Mock파일생성
        MockMultipartFile image = new MockMultipartFile(
                "imgUrl", //name
                fileName + "." + contentType, //originalFilename
                contentType,
                fileInputStream
        );

        Gson gson = new Gson();
        String json = gson.toJson(spaceVO);

        MockMultipartFile spaceVO1 = new MockMultipartFile(
                "spaceDTO", //name
                null, //originalFilename
                "application/json",
                json.getBytes()
        );

        log.info("json: " + json);

        mockMvc.perform(
                        MockMvcRequestBuilders.multipart("/space/")
                                .file(image)
                                .file(spaceVO1)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .characterEncoding("UTF-8"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void registerDefault() throws Exception{
        SpaceDTO spaceDTO = new SpaceDTO();

        spaceDTO.setSpaceName("디폴트");
        spaceDTO.setSpaceDescription("단위테스트중인 스페이스");
        spaceDTO.setSpacePw("00000");
        spaceDTO.setDefaultImg(0L);

        Gson gson = new Gson();
        String json = gson.toJson(spaceDTO);

        MockMultipartFile data = new MockMultipartFile(
                "spaceDTO", //name
                null, //originalFilename
                "application/json",
                json.getBytes()
        );

        MockMultipartFile imgUrl = new MockMultipartFile(
                "imgUrl", //name
                null, //originalFilename
                "application/json",
                "".getBytes()
        );

        log.info("json: " + json);

        mockMvc.perform(
                        MockMvcRequestBuilders.multipart("/space/")
                                .file(data)
                                .file(imgUrl)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
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
    void modify() throws Exception{
        SpaceDTO spaceDTO = new SpaceDTO();

        spaceDTO.setSpaceId(93L);
        spaceDTO.setSpaceName("디폴트 수정");
        spaceDTO.setSpaceDescription("수정완료");
        spaceDTO.setSpacePw("00000");
        spaceDTO.setDefaultImg(1L);

        Gson gson = new Gson();
        String json = gson.toJson(spaceDTO);

        MockMultipartFile data = new MockMultipartFile(
                "spaceDTO",
                null,
                "application/json",
                json.getBytes()
        );

        log.info("json: " + json);

        final MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/space/");

        builder.with(request -> {
            request.setMethod("PATCH");

            return request;
        });

        mockMvc.perform(
                        builder.file(data)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .characterEncoding("UTF-8"))
//                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
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
        SpaceUserDTO spaceUserDTO = new SpaceUserDTO();
        spaceUserDTO.setSpaceId(94L);
        spaceUserDTO.setUserNickname("수정한 회원");
        spaceUserDTO.setIsAdmin(false);

        Gson gson = new Gson();
        String json = gson.toJson(spaceUserDTO);

        final String fileName = "KakaoTalk_Photo_2023-04-14-17-58-36"; //파일명
        final String contentType = "jpeg"; //파일타입
        final String filePath = "/Users/son/Documents/마루/"+fileName+"."+contentType; //파일경로

        log.info("filePath: " + filePath);
        FileInputStream fileInputStream = new FileInputStream(filePath);

        // Mock파일생성
        MockMultipartFile image = new MockMultipartFile(
                "image", //name
                fileName + "." + contentType, //originalFilename
                contentType,
                fileInputStream
        );

        MockMultipartFile data = new MockMultipartFile(
                "spaceUserDTO", //name
                null, //originalFilename
                "application/json",
                json.getBytes()
        );

        log.info("json: " + json);

        final MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/space/user");

        builder.with(request -> {
            request.setMethod("PATCH");

            return request;
        });

        mockMvc.perform(
                        builder.file(data)
                                .file(image)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .characterEncoding("UTF-8"))
//                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void modifyAdmin() throws Exception{
        SpaceUserDTO spaceUserDTO = new SpaceUserDTO();
        spaceUserDTO.setIsAdmin(true);
        log.info("admin:" + spaceUserDTO.getIsAdmin());
        spaceUserDTO.setSpaceId(47L);

        Gson gson = new Gson();
        String json = gson.toJson(spaceUserDTO);

        MockMultipartFile data = new MockMultipartFile(
                "spaceUserDTO", //name
                null, //originalFilename
                "application/json",
                json.getBytes()
        );

        log.info("json: " + json);

        final MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/space/user");

        builder.with(request -> {
            request.setMethod("PATCH");

            return request;
        });

        mockMvc.perform(
                        builder.file(data)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .characterEncoding("UTF-8"))
//                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void filter() throws Exception{
        log.info("filter: " + mockMvc.perform(MockMvcRequestBuilders.get("/space/search")
                        .param("spaceId", "46")
//                         .param("tagId", "61")
//                        .param("keyword", "수정")
                        .param("startDate", "2023/11/05")
                        .param("page", "1"))
//                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print()));
    }
}