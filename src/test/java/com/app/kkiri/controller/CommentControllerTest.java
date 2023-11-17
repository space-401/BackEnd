package com.app.kkiri.controller;

import com.app.kkiri.domain.vo.CommentVO;
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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class CommentControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp(){ mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build(); }

    @Test
    void insert() throws Exception{
        CommentVO commentVO = new CommentVO();
        commentVO.create("댓글", false, 44L, 1L);
        commentVO.create("대댓글", true, 28L, 44L, 1L);

        Gson gson = new Gson();
        String json = gson.toJson(commentVO);

        log.info("insert: " + mockMvc.perform(MockMvcRequestBuilders.post("/comment")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print()));
    }

    @Test
    void delete() throws Exception{
        log.info("delete: " + mockMvc.perform(MockMvcRequestBuilders.delete("/comment")
                        .param("commentId","28"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print()));
    }

    @Test
    void list() throws Exception{
        log.info("list: " + mockMvc.perform(MockMvcRequestBuilders.get("/comment")
                        .param("postId", "44")
                        .param("spaceId", "46"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print()));
    }
}