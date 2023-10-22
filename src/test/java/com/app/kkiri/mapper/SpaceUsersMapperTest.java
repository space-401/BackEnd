package com.app.kkiri.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
class SpaceUsersMapperTest {
	@Autowired
	private SpaceUsersMapper spaceUsersMapper;

	@Test
	void selectAll() {
		spaceUsersMapper.selectAll(4L, 1L);
	}

	@Test
	void selectById() {
		spaceUsersMapper.selectById(21L, 1L);
	}

	// @Test
	// void selectByUserAdminYn() {
	// 	spaceUsersMapper.selectByUserAdminYn(4L, 3L);
	// }

	@Test
	void selectByFirst() {
		spaceUsersMapper.selectByFirst(4L, 1L);
	}

	// @Test
	// void insert() {
	// 	SpaceUserVO spaceUserVO = new SpaceUserVO();
	// 	spaceUserVO.createNormal(2L);
	// 	spaceUserVO.setSpaceId(46L);
	// 	spaceUsersMapper.insert(spaceUserVO);
	// }

	@Test
	void delete() {
		spaceUsersMapper.delete(42L, 1L);
	}

	// @Test
	// void update() {
	// 	SpaceUserVO spaceUserVO = spaceUsersMapper.selectById(21L, 1L);
	// 	spaceUserVO.setUserNickname("수정회원");
	// 	spaceUserVO.setProfileImgName("a");
	// 	spaceUserVO.setProfileImgPath("a");
	// 	spaceUserVO.setProfileImgUuid("a");
	// 	spaceUserVO.setProfileImgSize(12L);
	// 	spaceUsersMapper.update(spaceUserVO);
	// }

	@Test
	void updateByAdminYn() {
		spaceUsersMapper.updateByAdminYn(4L);
	}

	@Test
	void updateByUserId() {
		spaceUsersMapper.updateByUserId(4L, 1L);
	}

	@Test
	void selectByNickname() {
		spaceUsersMapper.selectByNickname(4L, "안녕");
	}
}