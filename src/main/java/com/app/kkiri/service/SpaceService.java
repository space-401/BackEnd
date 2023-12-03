package com.app.kkiri.service;

import static com.app.kkiri.global.exception.ExceptionCode.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.app.kkiri.domain.dto.response.UserResponseDTO;
import com.app.kkiri.domain.vo.UserVO;
import com.app.kkiri.repository.UsersDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.kkiri.domain.dto.SpaceDTO;
import com.app.kkiri.domain.dto.SpaceDetailDTO;
import com.app.kkiri.domain.dto.TagDTO;
import com.app.kkiri.domain.dto.response.SpaceResponseDTO;
import com.app.kkiri.domain.dto.response.SpaceUserRespnseDTO;
import com.app.kkiri.domain.dto.response.TagResponseDTO;
import com.app.kkiri.domain.vo.SpaceUserVO;
import com.app.kkiri.domain.vo.SpaceVO;
import com.app.kkiri.domain.vo.TagVO;
import com.app.kkiri.global.exception.BadRequestException;
import com.app.kkiri.global.exception.ExceptionCode;
import com.app.kkiri.repository.SpaceUsersDAO;
import com.app.kkiri.repository.SpacesDAO;
import com.app.kkiri.repository.TagsDAO;
import com.app.kkiri.security.jwt.JwtTokenProvider;

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
	private final UsersDAO usersDAO;
	private final JwtTokenProvider jwtTokenProvider;
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
		spaceDetailDTO.setSpaceCode(spaceVO.getSpaceCode());
		spaceDetailDTO.setIsAdmin(spaceUsersDAO.findByUserAdminYn(spaceId, userId));

		// isFirst인지 체크하는 로직
		// 처음인 경우 1, 처음이 아닌 경우 0
		SpaceUserVO spaceUserVO = spaceUsersDAO.findById(spaceId, userId);
		UserResponseDTO userResponseDTO = usersDAO.findById(userId);

		LOGGER.info("[spaceDetail()] spaceUserVO : {}", spaceUserVO);
		LOGGER.info("[spaceDetail()] userResponseDTO : {}", userResponseDTO);

		spaceDetailDTO.setIsFirst(spaceUserVO.getUserNickname().toString().equals(userResponseDTO.getUserEmail().toString()) ?  1 : 0);

		List<SpaceVO> spaces = spacesDAO.findAll(userId);
		for (SpaceVO space:spaces) {
			List<SpaceUserVO> userList = spaceUsersDAO.findAll(space.getSpaceId(), userId);
			List<SpaceUserRespnseDTO> spaceUserRespnseDTOList = new ArrayList<>();

			for (SpaceUserVO user:userList) {
				SpaceUserRespnseDTO spaceUserRespnseDTO = new SpaceUserRespnseDTO();
				spaceUserRespnseDTO.setUserId(user.getUserId());
				spaceUserRespnseDTO.setUserName(user.getUserNickname());
				spaceUserRespnseDTO.setImgUrl(fileService.getS3ObjectURL(user.getProfileImgPath()));

				spaceUserRespnseDTOList.add(spaceUserRespnseDTO);
			}

			spaceDetailDTO.setUserList(spaceUserRespnseDTOList);
		}

		List<TagVO> tags = Optional.ofNullable(tagsDAO.findAll(spaceId)).orElse(new ArrayList<>());
		List<TagDTO> tagList = new ArrayList<>();
		for (TagVO tag:tags) {
			TagDTO tagDTO = new TagDTO();
			tagDTO.setTagId(tag.getTagId());
			tagDTO.setTagName(tag.getTagName());

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
			throw new BadRequestException(INVALID_REQUEST);
		}
	}

	// 스페이스 태그 조회
	public  List<TagVO> tagList(Long spaceId){
		LOGGER.info("[tagList()] param spaceId : {}", spaceId);

		List<TagVO> tags = tagsDAO.findAll(spaceId);
		LOGGER.info("[tagList()] tags : {}", tags);

		return tags;
	}

	// 스페이스 태그 추가
	public TagResponseDTO addTag(TagVO tagVO){

		tagsDAO.save(tagVO);

		TagResponseDTO tagResponseDTO = tagsDAO.findRecentTag();
		LOGGER.info("[addTag()] tagResponseDTO : {}", tagResponseDTO);

		return tagResponseDTO;
	}

	// 스페이스 태그 삭제
	public void removeTag(Long tagId){

		tagsDAO.delete(tagId);
	}

	// 스페이스 회원 입장 (초대코드 입력)
	@Transactional(rollbackFor = Exception.class)
	public Long enter(Long userId, SpaceVO spaceVO){
		Long spaceId = spacesDAO.findByCodeAndPw(spaceVO).orElseThrow(()->new BadRequestException(ExceptionCode.INVALID_SPACE_CODE_OR_PASSWORD));

		if(spaceUsersDAO.findById(spaceId, userId) != null){
			 throw new BadRequestException(ExceptionCode.ALREADY_SAVED_SPACE);
		} else {
			SpaceUserVO spaceUserVO = new SpaceUserVO();
			spaceUserVO.createNormal(userId);
			spaceUserVO.setSpaceId(spaceId);
			spaceUserVO.setUserNickname(usersDAO.findById(userId).getUserEmail());
			spaceUsersDAO.save(spaceUserVO);
			spacesDAO.setTally(spaceId,spacesDAO.getTally(spaceId) + 1);
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
	public void modifyStatus(Long originalAdminUserId, Long newAdminUserId, Long spaceId){

		try{
			spaceUsersDAO.setByAdminYn(originalAdminUserId, spaceId);
			spaceUsersDAO.setByUserId(spaceId, newAdminUserId);
		} catch (Exception e){
			// throw new CustomException(StatusCode.BAD_REQUEST);
		}
	}

	// 스페이스 내 유저 정보 변경
	public void modifyInfo(SpaceUserVO spaceUserVO){
		LOGGER.info("[modifyInfo() param spaceUserVO : {}]", spaceUserVO);

		try{
			spaceUsersDAO.set(spaceUserVO);
		} catch (Exception e){
			// throw new CustomException(StatusCode.BAD_REQUEST);
		}
	}

	// userId 를 사용한 스페이스 회원 삭제
	@Transactional(rollbackFor = Exception.class)
	public void deleteSpaceUser(HttpServletRequest httpServletRequest) {

		Long userId = getUserId(httpServletRequest);

		List<Long> spaceIds = spaceUsersDAO.findSpaceIdByUserIdAndUserAdminYnTrue(userId);

		if(spaceIds.size() != 0) { // 방장인 경우
			for (Long spaceId : spaceIds) { // 방장인 스페이스를 순회한다
				if(spacesDAO.getTally(spaceId) != 1) { // 스페이스에 방장 혼자만 가입된 경우가 아니라면
					throw new BadRequestException(FAIL_TO_DELETE_USER); // 방장은 탈퇴 할 수 없다.
				}
			}
		}

		spaceUsersDAO.deleteByUserId(userId);
	}

	// userId 를 사용한 스페이스 회원 삭제
	@Transactional(rollbackFor = Exception.class)
	public void deleteSpace(HttpServletRequest httpServletRequest) {

		spacesDAO.deleteByUserId(getUserId(httpServletRequest));
	}

	private Long getUserId(HttpServletRequest httpServletRequest) {
		return jwtTokenProvider.getUserIdByHttpRequest(httpServletRequest);
	}

	// spaceId 를 사용하여 spaces 의 모든 컬럼을 조회
	public SpaceVO findBySpaceId(Long spaceId) {
		return spacesDAO.findBySpaceId(spaceId);
	}
}