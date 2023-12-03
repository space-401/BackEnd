package com.app.kkiri.controller;

import static com.app.kkiri.global.exception.ExceptionCode.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.servlet.http.HttpServletRequest;

import com.app.kkiri.domain.dto.PostFilterValueDTO;
import com.app.kkiri.domain.dto.response.UserResponseDTO;
import com.app.kkiri.service.UserService;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.app.kkiri.domain.dto.SpaceDTO;
import com.app.kkiri.domain.dto.SpaceDetailDTO;
import com.app.kkiri.domain.dto.response.PostFilterResponseDTO;
import com.app.kkiri.domain.dto.SpaceUserDTO;
import com.app.kkiri.domain.dto.response.TagResponseDTO;
import com.app.kkiri.domain.vo.SpaceUserVO;
import com.app.kkiri.domain.vo.SpaceVO;
import com.app.kkiri.domain.vo.TagVO;
import com.app.kkiri.global.exception.ImageException;
import com.app.kkiri.security.jwt.JwtTokenProvider;
import com.app.kkiri.service.FileService;
import com.app.kkiri.service.PostService;
import com.app.kkiri.service.SpaceService;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@RequestMapping("/space")
@Slf4j
public class SpaceController {

	private final Logger LOGGER = LoggerFactory.getLogger(SpaceController.class);
	private final SpaceService spaceService;
	private final PostService postService;
	private final JwtTokenProvider jwtTokenProvider;
	private final FileService fileService;
	private final UserService userService;

	@Value("${file.rootPath.space}")
	private String spaceRootPath;

	@Value("${file.rootPath.default}")
	private String defaultRootPath;

	@Value("${file.rootPath.profile}")
	private String profileRootPath;

	// 목록 조회
	@GetMapping("/list")
	public ResponseEntity<Map<String, Object>> list(HttpServletRequest request){

		Long userId = jwtTokenProvider.getUserIdByHttpRequest(request);

		Map<String, Object> map = new HashMap<>();
		map.put("spaceList", spaceService.list(userId));

		return ResponseEntity.ok().body(map);
	}

	// 스페이스 상세 조회
	@GetMapping("")
	public ResponseEntity<?> spaceDetail(@RequestParam Long spaceId, HttpServletRequest request){

		Long userId = jwtTokenProvider.getUserIdByHttpRequest(request);

		SpaceDetailDTO spaceDetailDTO = spaceService.spaceDetail(spaceId, userId);

		return ResponseEntity.ok().body(spaceDetailDTO);
	}

	// 스페이스 생성
	@PostMapping(value = "", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
	public ResponseEntity<?> register(
		@RequestPart SpaceDTO spaceDTO,
		@RequestPart(required = false, value = "imgUrl") MultipartFile multipartFile,
		HttpServletRequest request) throws IOException {
		LOGGER.info("[register()] param spaceDTO : {}, multipartFile : {}", spaceDTO, multipartFile);
		
		Long userId = jwtTokenProvider.getUserIdByHttpRequest(request);
		UserResponseDTO userResponseDTO = userService.search(userId);

		SpaceVO spaceVO = new SpaceVO();
		spaceVO.setSpaceName(spaceDTO.getSpaceName());
		spaceVO.setSpacePw(spaceDTO.getSpacePw());
		spaceVO.setSpaceDescription(spaceDTO.getSpaceDescription());
		spaceVO.setSpaceCode(UUID.randomUUID().toString());
		spaceVO.setSpaceUserTally(1);

		if(!multipartFile.isEmpty()){ // 사용자가 이미지를 지정한 경우
			StringBuffer uploadPath = new StringBuffer();
			StringBuffer uploadFileName = new StringBuffer();
			StringBuffer uploadFullPathAndFileName = new StringBuffer();

			uploadPath.append(spaceRootPath); // upload/space
			uploadPath.append("/");
			uploadPath.append(getUploadPath()); // 2023/11/10

			String uuid = UUID.randomUUID().toString();
			String originalFileName = multipartFile.getOriginalFilename();

			uploadFileName.append(uuid);
			uploadFileName.append("_");
			uploadFileName.append(originalFileName); // uuid_space.jpg

			uploadFullPathAndFileName.append(uploadPath);
			uploadFullPathAndFileName.append("/");
			uploadFullPathAndFileName.append(uploadFileName); // upload/space/2023/11/10/uuid_space.jpg

			fileService.uploadFileToS3(uploadFullPathAndFileName.toString(), multipartFile);

			spaceVO.setSpaceIconName(originalFileName); // space.jpg
			spaceVO.setSpaceIconPath(uploadFullPathAndFileName.toString()); // upload/space/2023/11/10/uuid_space.jpg
			spaceVO.setSpaceIconUuid(uuid); // uuid
			spaceVO.setSpaceIconSize(multipartFile.getSize()); // ...bytes
		} else { // 기본이미지를 사용한 경우
			Long defaultImg = spaceDTO.getDefaultImg();
			if(defaultImg != null){
				spaceVO.setSpaceIconName(defaultImg + ".jpg"); // 1.jpg
				// 디폴트 이미지 번호는 프론트에서 넘어온다. (0 ~ 4)

				spaceVO.setSpaceIconUuid("default"); // default
				spaceVO.setSpaceIconPath(defaultRootPath + "/" + defaultImg + ".jpg"); // upload/default/1.jpg
				spaceVO.setSpaceIconSize(0L); // 0L
			} else {
				throw new ImageException(MISSING_IMAGE);
			}
		}

		SpaceUserVO spaceUserVO = new SpaceUserVO();
		spaceUserVO.setUserId(userId);
		spaceUserVO.setUserAdminYn(true);
		spaceUserVO.setUserNickname(userResponseDTO.getUserEmail());
		spaceUserVO.setProfileImgName("defaultProfile.png");
		spaceUserVO.setProfileImgPath("upload/default/defaultProfile.png");
		spaceUserVO.setProfileImgUuid("default");
		// "" 빈문자열로 작성하면 DBMS 에서 null 로 인식하여 에러가 발생한다.
		// 따라서 임의의 문자열인 "default" 를 기본값으로 저장한다.

		spaceUserVO.setProfileImgSize(0L);

		Long spaceId = spaceService.register(spaceVO, spaceUserVO);

		String[] tagNames = spaceDTO.getTags();

		if(tagNames != null) {
			for (String tagName : tagNames) {
				TagVO tagVO = TagVO.builder()
					.tagName(tagName)
					.spaceId(spaceId)
					.build();

				TagResponseDTO tagResponseDTO = spaceService.addTag(tagVO);
				LOGGER.info("[register()] tagResponseDTO : {}", tagResponseDTO);
			}
		}

		Map<String, Object> map = new HashMap<>();
		map.put("spaceId", spaceId);

		return ResponseEntity.status(HttpStatus.CREATED).body(map);
	}

	// 스페이스 삭제
	@DeleteMapping("")
	public ResponseEntity remove(@RequestParam Long spaceId){

		spaceService.remove(spaceId);

		return ResponseEntity.noContent().build();
	}

	// 스페이스 수정
	@PatchMapping("")
	public ResponseEntity modify(
		@RequestPart SpaceDTO spaceDTO,
		@RequestPart(required = false, value = "imgUrl") MultipartFile multipartFile) throws IOException {
		LOGGER.info("[modify()] params spaceDTO : {}, multipartFile : {}", spaceDTO, multipartFile);

		if(!multipartFile.isEmpty()) { // 사용자가 이미지를 지정한 경우
			StringBuffer uploadPath = new StringBuffer();
			StringBuffer uploadFileName = new StringBuffer();
			StringBuffer uploadFullPathAndFileName = new StringBuffer();

			uploadPath.append(spaceRootPath); // upload/space
			uploadPath.append("/");
			uploadPath.append(getUploadPath()); // 2023/11/10

			String uuid = UUID.randomUUID().toString();
			String originalFileName = multipartFile.getOriginalFilename();

			uploadFileName.append(uuid);
			uploadFileName.append("_");
			uploadFileName.append(originalFileName); // uuid_space.jpg

			uploadFullPathAndFileName.append(uploadPath);
			uploadFullPathAndFileName.append("/");
			uploadFullPathAndFileName.append(uploadFileName); // upload/space/2023/11/10/uuid_space.jpg

			fileService.uploadFileToS3(uploadFullPathAndFileName.toString(), multipartFile);

			spaceDTO.setSpaceIconName(originalFileName); // space.jpg
			spaceDTO.setSpaceIconPath(uploadFullPathAndFileName.toString()); // upload/space/2023/11/10/uuid_space.jpg
			spaceDTO.setSpaceIconUuid(uuid); // uuid
			spaceDTO.setSpaceIconSize(multipartFile.getSize()); // ...bytes
		} else if(spaceDTO.getDefaultImg() != null) { // 사용자가 디폴트 이미지를 선택한 경우
			Long defaultImg = spaceDTO.getDefaultImg();
			spaceDTO.setSpaceIconName(defaultImg + ".jpg"); // 1.jpg
			spaceDTO.setSpaceIconUuid("default"); // default
			spaceDTO.setSpaceIconPath(defaultRootPath + "/" + defaultImg + ".jpg"); // upload/default/1.jpg
			spaceDTO.setSpaceIconSize(0L); // 0L
		} else { // 사용자가 이미지 변경을 원하지 않는 경우
			SpaceVO selectedSpace = spaceService.findBySpaceId(spaceDTO.getSpaceId());
			spaceDTO.setSpaceIconName(selectedSpace.getSpaceIconName());
			spaceDTO.setSpaceIconUuid(selectedSpace.getSpaceIconUuid());
			spaceDTO.setSpaceIconPath(selectedSpace.getSpaceIconPath());
			spaceDTO.setSpaceIconSize(selectedSpace.getSpaceIconSize());
		}

		spaceService.modify(spaceDTO);

		Map<String, Object> map = new HashMap<>();
		map.put("spaceId", spaceDTO.getSpaceId());

		return ResponseEntity.status(HttpStatus.CREATED).body(map);
	}

	// 스페이스 태그 조회
	@GetMapping("/tag")
	public ResponseEntity<Map<String, Object>> tagList(@RequestParam Long spaceId){

		List<TagVO> tags = spaceService.tagList(spaceId);

		Map<String, Object> map = new HashMap<>();
		map.put("tags", tags);

		return ResponseEntity.ok().body(map);
	}

	// 스페이스 태그 추가
	@PostMapping("/tag")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public ResponseEntity addTag(@RequestBody TagVO tagVO){

		TagResponseDTO tagResponseDTO = spaceService.addTag(tagVO);

		return ResponseEntity.status(HttpStatus.CREATED).body(tagResponseDTO);
	}

	// 스페이스 태그 삭제
	@DeleteMapping("/tag")
	public ResponseEntity removeTag(@RequestParam Long spaceId, @RequestParam Long tagId){
		LOGGER.info("[removeTag()] param spaceId : {}, tagId : {}", spaceId, tagId);

		spaceService.removeTag(tagId);

		return ResponseEntity.noContent().build();
	}

	// 스페이스 회원 입장 (초대코드 입력)
	@PostMapping("/user")
	public ResponseEntity<Map<String, Object>> enterSpace(@RequestBody SpaceVO spaceVO, HttpServletRequest request){
		LOGGER.info("[enterSpace] param spaceVO : {}, request : {}", spaceVO, request);

		Long userId = jwtTokenProvider.getUserIdByHttpRequest(request);
		Long spaceId = spaceService.enter(userId, spaceVO);

		Map<String, Object> map = new HashMap<>();
		map.put("spaceId", spaceId);

		return ResponseEntity.status(HttpStatus.CREATED).body(map);
	}

	// 스페이스 회원 탈퇴
	@DeleteMapping("/user")
	public ResponseEntity withdrawSpace(@RequestParam Long spaceId, @RequestParam Long userId){
		LOGGER.info("[withdrawSpace()] param spaceId : {}, userId : {}", spaceId, userId);

		spaceService.withdrawSpace(spaceId, userId);

		return ResponseEntity.noContent().build();
	}

	// 스페이스 내 유저 정보 변경
	@PatchMapping(value = "/user", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
	public ResponseEntity modifyInfo(
		@RequestPart SpaceUserDTO spaceUserDTO,
		@RequestPart(required = false, value = "image") MultipartFile multipartFile,
		HttpServletRequest request) throws IOException {
		LOGGER.info("[modifyInfo()] spaceUserDTO : {}, multipartFile : {}, request : {}", spaceUserDTO, multipartFile, request);

		Long originalAdminUserId = jwtTokenProvider.getUserIdByHttpRequest(request);
		Long newAdminUserId = spaceUserDTO.getUserId();
		Long spaceId = spaceUserDTO.getSpaceId();

		SpaceUserVO spaceUserVO = new SpaceUserVO();
		spaceUserVO.setUserId(originalAdminUserId);

		if(spaceUserDTO.getIsAdmin()) { // 방장 변경
			spaceService.modifyStatus(originalAdminUserId, newAdminUserId, spaceId);
			return ResponseEntity.noContent().build();
		}

		if(!multipartFile.isEmpty()) { // 사용자가 이미지를 지정한 경우
			StringBuffer uploadPath = new StringBuffer();
			StringBuffer uploadFileName = new StringBuffer();
			StringBuffer uploadFullPathAndFileName = new StringBuffer();

			uploadPath.append(profileRootPath); // upload/profile
			uploadPath.append("/");
			uploadPath.append(getUploadPath()); // 2023/11/10

			String uuid = UUID.randomUUID().toString();
			String originalFileName = multipartFile.getOriginalFilename();

			uploadFileName.append(uuid);
			uploadFileName.append("_");
			uploadFileName.append(originalFileName); // uuid_profile.jpg

			uploadFullPathAndFileName.append(uploadPath);
			uploadFullPathAndFileName.append("/");
			uploadFullPathAndFileName.append(uploadFileName); // upload/profile/2023/11/10/uuid_profile.jpg

			fileService.uploadFileToS3(uploadFullPathAndFileName.toString(), multipartFile);

			spaceUserVO.setProfileImgName(originalFileName); // profile.jpg
			spaceUserVO.setProfileImgPath(uploadFullPathAndFileName.toString()); // upload/profile/2023/11/10/uuid_profile.jpg
			spaceUserVO.setProfileImgUuid(uuid); // uuid
			spaceUserVO.setProfileImgSize(multipartFile.getSize()); // ...bytes
		} else { // 사용자가 어떠한 이미지도 선택하지 않은 경우 기본 이미지를 저장한다.
			spaceUserVO.setProfileImgName("defaultProfile.png"); // defaultProfile.png
			spaceUserVO.setProfileImgPath(defaultRootPath + "/defaultProfile.png"); // upload/default/defaultProfile.png
			spaceUserVO.setProfileImgUuid("default"); // default
			spaceUserVO.setProfileImgSize(0L); // 0L
		}

		spaceUserVO.setSpaceId(spaceId);
		spaceUserVO.setUserNickname(spaceUserDTO.getUserNickname());

		spaceService.modifyInfo(spaceUserVO);

		return ResponseEntity.noContent().build();
	}

	// 게시글 필터 조회
	@PostMapping(value = "/search")
	public ResponseEntity<PostFilterResponseDTO> filter(
			@RequestBody(required = false) PostFilterValueDTO data,
			HttpServletRequest request){
		Long id = jwtTokenProvider.getUserIdByHttpRequest(request);

		LOGGER.info("[filter()] searchValue : {}", data);

		Map<String, Object> param = new HashMap<>();
		List<LocalDate> dateList = new ArrayList<>();
		int amount = 10;

		param.put("spaceId", data.getSpaceId());
		param.put("page", data.getPage().intValue());
		param.put("writers", data.getUserId());
		param.put("tags", data.getTagId());
		param.put("keyword", data.getKeyword());

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		if(data.getStartDate() != null && data.getEndDate() !=null){
			LocalDate start = LocalDate.parse(data.getStartDate(), formatter);
			LocalDate end = LocalDate.parse(data.getEndDate(), formatter);

			Long numOfDaysBetween = ChronoUnit.DAYS.between(start, end);
			dateList = IntStream.iterate(0, i -> i + 1)
			.limit(numOfDaysBetween)
			.mapToObj(i -> start.plusDays(i))
			.collect(Collectors.toList());
		} else if (data.getStartDate() != null) {
			dateList.add(LocalDate.parse(data.getStartDate(), formatter));
		} else if(data.getEndDate() != null){
			dateList.add(LocalDate.parse(data.getEndDate(), formatter));
		}

		param.put("dateList", dateList);
		param.put("amount", amount);

		return ResponseEntity.ok().body(postService.filter(param, id));
	}

	// 사진 업로드 위치 (해당 이미지를 업로드한 년/월/일)
	private String getUploadPath(){

		String uploadpath = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
		LOGGER.info("[getUploadPath()] uploadpath : {}", uploadpath);

		return uploadpath;
	}
}
