package com.app.kkiri.service;

import com.app.kkiri.domain.vo.*;
import com.app.kkiri.exceptions.CustomException;
import com.app.kkiri.exceptions.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.app.kkiri.repository.SpaceUsersDAO;
import com.app.kkiri.repository.SpacesDAO;
import com.app.kkiri.repository.TagsDAO;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SpaceService {
	private final SpacesDAO spacesDAO;
	private final SpaceUsersDAO spaceUsersDAO;
	private final TagsDAO tagsDAO;

	// 목록 조회
	public List<SpaceListDTO> list(Long userId){
		List<SpaceListDTO> spaceList = new ArrayList<>();
		List<SpaceVO> spaces = new ArrayList<>();

		spaces = spacesDAO.findAll(userId);

		for (SpaceVO space : spaces) {
			SpaceListDTO spaceListDTO = new SpaceListDTO();
			spaceListDTO.setSpaceId(space.getSpaceId());
			spaceListDTO.setSpaceName(space.getSpaceName());
			spaceListDTO.setSpaceIconPath(space.getSpaceIconPath());
			spaceListDTO.setSpaceUsers(spaceUsersDAO.findAll(space.getSpaceId(), userId));

			spaceList.add(spaceListDTO);
		}

		return spaceList;
	};

	// 스페이스 상세 조회
	public SpaceDetailDTO spaceDetail(Long spaceId, Long userId){
		SpaceDetailDTO spaceDetailDTO = new SpaceDetailDTO();
		SpaceVO spaceVO = new SpaceVO();
		List<SpaceUserVO> spaceUsers = new ArrayList<>();
		List<TagVO> tags = new ArrayList<>();

		spaceVO = spacesDAO.findById(spaceId);
		spaceUsers = spaceUsersDAO.findAll(spaceId, userId);
		tags = Optional.ofNullable(tagsDAO.findAll(spaceId)).orElse(new ArrayList<>());

		spaceDetailDTO.setSpacePw(spaceVO.getSpacePw());
		spaceDetailDTO.setSpaceDescription(spaceVO.getSpaceDescription());
		spaceDetailDTO.setSpaceName(spaceVO.getSpaceName());
		spaceDetailDTO.setSpaceIconPath(spaceVO.getSpaceIconPath());
		spaceDetailDTO.setSpaceUsers(spaceUsers);
		spaceDetailDTO.setIsAdmin(spaceUsersDAO.findByUserAdminYn(spaceId, userId));
		spaceDetailDTO.setIsFirst(spaceUsersDAO.findByFirst(spaceId, userId) == 0 ? 1 : 0);
		spaceDetailDTO.setTags(tags);

		return spaceDetailDTO;
	};

	// 스페이스 생성
	@Transactional(rollbackFor = Exception.class)
	public Long register(SpaceVO spaceVO, SpaceUserVO spaceUserVO){
		spacesDAO.save(spaceVO);
		spaceUserVO.setSpaceId(spaceVO.getSpaceId());
		spaceUsersDAO.save(spaceUserVO);
		return spaceVO.getSpaceId();
	};

	// 스페이스 삭제
	public void remove(Long spaceId){
		try {
			spacesDAO.delete(spaceId);
		}catch(Exception e){
			throw new CustomException(StatusCode.BAD_REQUEST);
		}
	};

	// 스페이스 수정
	public void modify(SpaceVO spaceVO){
		try {
			spacesDAO.set(spaceVO);
		}catch(Exception e){
			throw new CustomException(StatusCode.BAD_REQUEST);
		}
	};

	// 게시글 필터 조회
	public void filter(){
	};

	// 스페이스 태그 조회
	public  List<TagVO> tagList(Long spaceId){
		return tagsDAO.findAll(spaceId);
	};

	// 스페이스 태그 추가
	public void addTag(TagVO tagVO){
		try {
			tagsDAO.save(tagVO);
		} catch (Exception e){
			throw new CustomException(StatusCode.BAD_REQUEST);
		}
	};

	// 스페이스 태그 삭제
	public void removeTag(Long tagId){
		try {
			tagsDAO.delete(tagId);
		} catch (Exception e){
			throw new CustomException(StatusCode.BAD_REQUEST);
		}
	};

	@Transactional(rollbackFor = Exception.class)
	// 스페이스 회원 입장 (초대코드 입력)
	public Long enter(Long userId, SpaceVO spaceVO){
		Long spaceId = spacesDAO.findByCodeAndPw(spaceVO);

		if(spaceId != null){
			if(spaceUsersDAO.findById(spaceId, userId) != null){
				log.info("유저가 가입되어있는 경우");
				throw new CustomException(StatusCode.ALREADY_SAVED_SPACE);
			} else {
				SpaceUserVO spaceUserVO = new SpaceUserVO();
				spaceUserVO.createNormal(userId);
				spaceUserVO.setSpaceId(spaceId);
				spaceUsersDAO.save(spaceUserVO);
			}
		} else {
			log.info("비밀번호가 틀린 경우");
			throw new CustomException(StatusCode.BAD_REQUEST);
		}
		return spaceId;
	};

	// 스페이스 회원 탈퇴
	public void withdrawSpace(Long spaceId, Long userId){
		try {
			spaceUsersDAO.delete(spaceId, userId);
		} catch (Exception e){
			throw new CustomException(StatusCode.BAD_REQUEST);
		}
	};

	// 스페이스 회원 상태 변경
	@Transactional(rollbackFor = Exception.class)
	public void modifyStatus(Long spaceId, Long userId){
		try{
			spaceUsersDAO.setByAdminYn(spaceId);
			spaceUsersDAO.setByUserId(spaceId, userId);
		} catch (Exception e){
			throw new CustomException(StatusCode.BAD_REQUEST);
		}
	};


	// 스페이스 내 유저 정보 변경
	public void modifyInfo(SpaceUserVO spaceUserVO){
		try{
			spaceUsersDAO.set(spaceUserVO);
		} catch (Exception e){
			throw new CustomException(StatusCode.BAD_REQUEST);
		}
	};
}
