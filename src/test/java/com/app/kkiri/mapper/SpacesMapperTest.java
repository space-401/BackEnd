package com.app.kkiri.mapper;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.app.kkiri.domain.vo.SpaceVO;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class SpacesMapperTest {
	@Autowired
	private SpacesMapper spacesMapper;

	@Test
	void selectAll() {
		spacesMapper.selectAll(1L);
	}

	@Test
	void selectById() {
		spacesMapper.selectById(4L);
	}

	@Test
	void selectByCodeAndPw(){
		SpaceVO spaceVO = new SpaceVO();
		spaceVO.setSpaceId(4L);
		spaceVO.setSpaceCode("2afc4e14-4bfd-43c8-9364-6a21c83edacf");
		spaceVO.setSpacePw("00010");
		spacesMapper.selectByCodeAndPw(spaceVO); }

	@Test
	void insert() {
		SpaceVO spaceVO = new SpaceVO();
		String uuid = UUID.randomUUID().toString();
		spaceVO.create("테스트6", "테스트중인 스페이스", "00000", uuid, "a", "a", "a", 12L);
		spacesMapper.insert(spaceVO);
	}

	@Test
	void delete() {
		spacesMapper.delete(21L);
	}

	@Test
	void update() {
		SpaceVO spaceVO = spacesMapper.selectById(4L);
		spaceVO.setSpaceId(4L);
		spaceVO.setSpaceName("수정스페이스");
		spaceVO.setSpaceDescription("설명 수정");
		spaceVO.setSpacePw("00000");
		spaceVO.setSpaceUserTally(2);
		spaceVO.setSpaceIconName("a");
		spaceVO.setSpaceIconPath("a");
		spaceVO.setSpaceIconUuid("a");
		spaceVO.setSpaceIconSize(12L);
		spacesMapper.update(spaceVO);
	}

	@Test
	void getTally(){
		spacesMapper.getTally(4L);
	}

	@Test
	void updateTally(){
		SpaceVO spaceVO = new SpaceVO();
		spaceVO.setSpaceUserTally(spacesMapper.getTally(4L) + 1);
		spaceVO.setSpaceId(4L);
		spacesMapper.updateTally(spaceVO);

	}
}