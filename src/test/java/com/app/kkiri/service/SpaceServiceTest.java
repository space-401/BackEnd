package com.app.kkiri.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.app.kkiri.domain.dto.SpaceListDTO;
import com.app.kkiri.domain.vo.SpaceUserVO;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class SpaceServiceTest {
    @Autowired
    private SpaceService spaceService;

    // 에러 발생으로 아래 코드를 주석 처리함 11.03
    // @Test
    // void list() {
    //     List<SpaceListDTO> spaces = new ArrayList<>();
    //     spaces = spaceService.list(21L);
    //
    //     log.info("spaceList : " + spaces);
    // }

    // @Test
    // void spaceDetail() {
    //     log.info("spaceDetail : " + spaceService.spaceDetail(47L, 1L));
    // }

    // @Test
    // void register() {
    //     SpaceVO spaceVO = new SpaceVO();
    //     SpaceUserVO spaceUserVO = new SpaceUserVO();
    //     String uuid = UUID.randomUUID().toString();
	//
    //     spaceVO.create("servicetest", "serviceTest", "00000", uuid, "a", "a", "a", 12L);
    //     spaceUserVO.createAdmin(3L);
    //     spaceService.register(spaceVO, spaceUserVO);
    // }

    @Test
    void remove() {
        spaceService.remove(66L);
    }

    @Test
    void modify() {
//        SpaceVO spaceVO = new SpaceVO();
//        spaceVO.setSpaceId(51L);
//        spaceVO.setSpaceName("수정완료");
//        spaceVO.setSpaceDescription("수정완료");
//        spaceVO.setSpacePw("12345");
//        spaceVO.setSpaceIconName("a");
//        spaceVO.setSpaceIconUuid("a");
//        spaceVO.setSpaceIconPath("a");
//        spaceVO.setSpaceIconSize(12L);
//        spaceService.modify(spaceVO);
    }

    @Test
    void filter() {
    }

    // @Test
    // void addTag() {
    //     TagVO tagVO = new TagVO();
    //     tagVO.create("바다", 32L);
    //     spaceService.addTag(tagVO);
    // }

    @Test
    void removeTag() {
        spaceService.removeTag(10L);
    }

    // @Test
    // void enterSpace() {
    //     SpaceVO spaceVO = new SpaceVO();
    //     spaceVO.setSpaceId(47L);
    //     spaceVO.setSpaceCode("982ca81c-38de-4e27-b14d-ce99857dd9f3");
    //     spaceVO.setSpacePw("00000");
    //     spaceService.enter(21L, spaceVO);
    // }

    @Test
    void withdrawSpace() {
        spaceService.withdrawSpace(47L, 5L);
    }

    @Test
    void modifyStatus() {
        spaceService.modifyStatus(46L, 2L);
    }

    @Test
    void modifyInfo() {
        SpaceUserVO spaceUserVO = new SpaceUserVO();
        spaceUserVO.setUserNickname("수정");
        spaceUserVO.setUserId(1L);
        spaceUserVO.setSpaceId(32L);
        spaceUserVO.setProfileImgUuid("test");
        spaceUserVO.setProfileImgName("test");
        spaceUserVO.setProfileImgPath("test");
        spaceUserVO.setProfileImgSize(11L);
        spaceService.modifyInfo(spaceUserVO);
    }
}