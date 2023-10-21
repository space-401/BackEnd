package com.app.kkiri.controller;

import com.app.kkiri.domain.vo.TagVO;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

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
        tagVO.setTagName("생일");
        tagVO.setSpaceId(44L);

        Gson gson = new Gson();
        String json = gson.toJson(tagVO);

        log.info("tagDTO: " + tagVO);
        log.info("json: " + json);
        log.info("remove: " + mockMvc.perform(MockMvcRequestBuilders.post("/space/tag")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print()));
    }


    @Test
    void removeTag() {
    }

    @Test
    void enterSpace() {
    }

    @Test
    void withdrawSpace() {
    }

    @Test
    void modifyInfo() {
    }

    @Test
    void testList() {
    }

    @Test
    void testSpaceDetail() {
    }

    @Test
    void testRegister() {
    }

    @Test
    void testRemove() {
    }

    @Test
    void testModify() {
    }

    @Test
    void testFilter() {
    }

    @Test
    void testTagList() {
    }

    @Test
    void testAddTag() {
    }

    @Test
    void testRemoveTag() {
    }

    @Test
    void testEnterSpace() {
    }

    @Test
    void testWithdrawSpace() {
    }

    @Test
    void testModifyInfo() {
    }
}