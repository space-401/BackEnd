package com.app.kkiri.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.kkiri.domain.dto.SpaceDTO;
import com.app.kkiri.domain.dto.SpaceDetailDTO;
import com.app.kkiri.domain.dto.SpaceListDTO;
import com.app.kkiri.domain.dto.response.SpaceResponseDTO;
import com.app.kkiri.domain.dto.response.SpaceUserRespnseDTO;
import com.app.kkiri.domain.dto.TagDTO;
import com.app.kkiri.domain.dto.response.TagResponse;
import com.app.kkiri.domain.vo.SpaceUserVO;
import com.app.kkiri.domain.vo.SpaceVO;
import com.app.kkiri.domain.vo.TagVO;
import com.app.kkiri.global.exception.BadRequestException;
import com.app.kkiri.global.exception.ExceptionCode;
import com.app.kkiri.repository.SpaceUsersDAO;
import com.app.kkiri.repository.SpacesDAO;
import com.app.kkiri.repository.TagsDAO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SpaceService {

	private final SpacesDAO spacesDAO;
	private final SpaceUsersDAO spaceUsersDAO;
	private final TagsDAO tagsDAO;
	private final FileService fileService;
	private final Logger LOGGER = LoggerFactory.getLogger(SpaceService.class);

	// 목록 조회
	public List<SpaceResponseDTO> list(Long userId){
		// 스페이스 정보, 상단 바에 뜨는 스페이스 정보 및 스페이스 유저 목록을 담는 객체
		List<SpaceResponseDTO> spaceList = new ArrayList<>();
		// 유저가 가입되어있는 스페이스 목록 객체
		List<SpaceVO> spaces = spacesDAO.findAll(userId);

		// 유저가 가입되어 있는 각 스페이스 목록의 정보를 Response 형식에 맞춰 변경
		for (SpaceVO space : spaces) {
			SpaceResponseDTO spaceResponseDTO = new SpaceResponseDTO();
			spaceResponseDTO.setSpaceId(space.getSpaceId());
			spaceResponseDTO.setSpaceTitle(space.getSpaceName());
			spaceResponseDTO.setImgUrl(fileService.getS3ObjectURL(space.getSpaceIconPath()));

			List<SpaceUserVO> users = spaceUsersDAO.findAll(space.getSpaceId(), userId);
			List<SpaceUserRespnseDTO> userList = new ArrayList<>();

			for (SpaceUserVO user:users) {
				SpaceUserRespnseDTO spaceUserRespnseDTO = new SpaceUserRespnseDTO();
				spaceUserRespnseDTO.setUserId(user.getUserId());
				spaceUserRespnseDTO.setUserName(user.getUserNickname());
				spaceUserRespnseDTO.setImgUrl(fileService.getS3ObjectURL(user.getProfileImgPath()));

				userList.add(spaceUserRespnseDTO);
			}
			spaceResponseDTO.setUserList(userList);
			spaceList.add(spaceResponseDTO);
		}

		return spaceList;
	}

	// 스페이스 상세 조회
	public SpaceDetailDTO spaceDetail(Long spaceId, Long userId){
		// spaceId인 스페이스의 정보를 담는다.
		SpaceVO spaceVO = spacesDAO.findById(spaceId);

		// Response 객체 형식에 맞춰 변경
		SpaceDetailDTO spaceDetailDTO = new SpaceDetailDTO();

		spaceDetailDTO.setSpaceTitle(spaceVO.getSpaceName());
		spaceDetailDTO.setSpaceDescription(spaceVO.getSpaceDescription());
		spaceDetailDTO.setImgUrl(fileService.getS3ObjectURL(spaceVO.getSpaceIconPath()));
		spaceDetailDTO.setSpacePw(spaceVO.getSpacePw());
		spaceDetailDTO.setIsAdmin(spaceUsersDAO.findByUserAdminYn(spaceId, userId));
		spaceDetailDTO.setIsFirst(spaceUsersDAO.findByFirst(spaceId, userId) == 0 ? 1 : 0);

		List<SpaceVO> spaces = spacesDAO.findAll(userId);
		List<SpaceListDTO> spaceListDTOList = new ArrayList<>();
		for (SpaceVO space:spaces) {
			SpaceListDTO spaceListDTO = new SpaceListDTO();
			spaceListDTO.setSpaceId(space.getSpaceId());
			spaceListDTO.setSpaceTitle(space.getSpaceName());
			spaceListDTO.setImgUrl(fileService.getS3ObjectURL(space.getSpaceIconPath()));

			List<SpaceUserVO> userList = spaceUsersDAO.findAll(space.getSpaceId(), userId);
			List<SpaceUserRespnseDTO> spaceUserRespnseDTOList = new ArrayList<>();

			for (SpaceUserVO user:userList) {
				SpaceUserRespnseDTO spaceUserRespnseDTO = new SpaceUserRespnseDTO();
				spaceUserRespnseDTO.setUserId(user.getUserId());
				spaceUserRespnseDTO.setUserName(user.getUserNickname());
				spaceUserRespnseDTO.setImgUrl(fileService.getS3ObjectURL(user.getProfileImgPath()));

				spaceUserRespnseDTOList.add(spaceUserRespnseDTO);
			}

			spaceListDTO.setUserList(spaceUserRespnseDTOList);

			spaceListDTOList.add(spaceListDTO);
		}
		spaceDetailDTO.setUserList(spaceListDTOList);

		List<TagVO> tags = Optional.ofNullable(tagsDAO.findAll(spaceId)).orElse(new ArrayList<>());
		List<TagDTO> tagList = new ArrayList<>();
		for (TagVO tag:tags) {
			TagDTO tagDTO = new TagDTO();
			tagDTO.setTagId(tag.getTagId());
			tagDTO.setTagTitle(tag.getTagName());

			tagList.add(tagDTO);
		}
		spaceDetailDTO.setTagList(tagList);

		return spaceDetailDTO;
	}

	// 스페이스 생성
	@Transactional(rollbackFor = Exception.class)
	public Long register(SpaceVO spaceVO, SpaceUserVO spaceUserVO){
		LOGGER.info("[register()] parmas spaceVO : {} spaceUserVO : {}", spaceVO, spaceUserVO);

		spacesDAO.save(spaceVO);

		Long spaceId = spaceVO.getSpaceId();
		LOGGER.info("[register()] spaceId : {}", spaceId);

		spaceUserVO.setSpaceId(spaceId);
		LOGGER.info("[register()] spaceUserVO : {}", spaceUserVO);

		spaceUsersDAO.save(spaceUserVO);

		return spaceId;
	}

	// 스페이스 삭제
	public void remove(Long spaceId){
		try {
			spacesDAO.delete(spaceId);
		}catch(Exception e){
			// throw new CustomException(StatusCode.BAD_REQUEST);
		}
	}

	// 스페이스 수정
	public void modify(SpaceDTO spaceDTO){
		try {
			spacesDAO.set(spaceDTO);
		}catch(Exception e){
			// throw new CustomException(StatusCode.BAD_REQUEST);
		}
	}

	// 스페이스 태그 조회
	public  List<TagVO> tagList(Long spaceId){

		List<TagVO> tags = tagsDAO.findAll(spaceId);
		LOGGER.info("[tagList()] tags : {}", tags);

		return tags;
	}

	// 스페이스 태그 추가
	@Transactional
	public TagResponse addTag(TagVO tagVO){

		tagsDAO.save(tagVO);

		TagResponse tagResponseDTO = tagsDAO.findRecentTag();
		LOGGER.info("[addTag()] tagResponseDTO : {}", tagResponseDTO);

		return tagResponseDTO;
	}

	// 디폴트 스페이스 태그 추가
	@Transactional
	public void addDefaultTags(List<TagVO> defaultTags){

		for(TagVO tagVO : defaultTags) {
			LOGGER.info("[addDefaultTags()] param tagVO : {}", tagVO);

			tagsDAO.save(tagVO);
		}
	}

	// 스페이스 태그 삭제
	public void removeTag(Long tagId){

		tagsDAO.delete(tagId);
	}

	// 스페이스 회원 입장 (초대코드 입력)
	@Transactional(rollbackFor = Exception.class)
	public Long enter(Long userId, SpaceVO spaceVO){
		Long spaceId = spacesDAO.findByCodeAndPw(spaceVO);

		if(spaceId != null){
			if(spaceUsersDAO.findById(spaceId, userId) != null){
				// throw new BadRequestException(ExceptionCode.ALREADY_SAVED_SPACE);
			} else {
				SpaceUserVO spaceUserVO = new SpaceUserVO();
				spaceUserVO.createNormal(userId);
				spaceUserVO.setSpaceId(spaceId);
				spaceUsersDAO.save(spaceUserVO);
				spacesDAO.setTally(spaceId,spacesDAO.getTally(spaceId) + 1);
			}
		} else {
			log.info("비밀번호가 틀린 경우");
			// throw new CustomException(StatusCode.BAD_REQUEST);
		}
		return spaceId;
	}

	// 스페이스 회원 탈퇴
	public void withdrawSpace(Long spaceId, Long userId){
		try {
			spaceUsersDAO.delete(spaceId, userId);
		} catch (Exception e){
			// throw new CustomException(StatusCode.BAD_REQUEST);
		}
	}

	// 스페이스 회원 상태 변경
	@Transactional(rollbackFor = Exception.class)
	public void modifyStatus(Long spaceId, Long userId){
		try{
			spaceUsersDAO.setByAdminYn(spaceId);
			spaceUsersDAO.setByUserId(spaceId, userId);
		} catch (Exception e){
			// throw new CustomException(StatusCode.BAD_REQUEST);
		}
	}

	// 스페이스 내 유저 정보 변경
	public void modifyInfo(SpaceUserVO spaceUserVO){
		try{
			spaceUsersDAO.set(spaceUserVO);
		} catch (Exception e){
			// throw new CustomException(StatusCode.BAD_REQUEST);
		}
	}
}