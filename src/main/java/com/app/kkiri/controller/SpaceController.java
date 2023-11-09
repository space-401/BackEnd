package com.app.kkiri.controller;

import java.io.File;
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
import com.app.kkiri.domain.dto.SpaceResponseDTO;
import com.app.kkiri.domain.dto.SpaceUserDTO;
import com.app.kkiri.domain.vo.SpaceUserVO;
import com.app.kkiri.domain.vo.SpaceVO;
import com.app.kkiri.domain.vo.TagVO;
import com.app.kkiri.exceptions.CustomException;
import com.app.kkiri.exceptions.StatusCode;
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

	private final SpaceService spaceService;
	private final PostService postService;
	private final JwtTokenProvider jwtTokenProvider;
	private final FileService fileService;
	private final Logger LOGGER = LoggerFactory.getLogger(SpaceController.class);

	@Value("${file.rootPath.space}")
	private String spaceRootPath;

	@Value("${file.rootPath.default}")
	private String defaultRootPath;

	@Value("${file.rootPath.profile}")
	private String profileRootPath;

	private Long getUserId(HttpServletRequest request){
		try{
			String token = jwtTokenProvider.resolveToken(request);
			return  jwtTokenProvider.getUserId(token);
		} catch (Exception e){
			throw new CustomException(StatusCode.INVALID_TOKEN);
		}
	}

	// 목록 조회
	@GetMapping("/list")
	public ResponseEntity<?> list(HttpServletRequest request){
		Long userId = getUserId(request);
		List<SpaceResponseDTO> spaceList = spaceService.list(userId);
		return ResponseEntity.ok().body(spaceList);
	}

	// 스페이스 상세 조회
	@GetMapping("")
	public ResponseEntity<?> spaceDetail(@RequestParam Long spaceId, HttpServletRequest request){
//		Long userId = 1L;
		Long userId = getUserId(request);
		SpaceDetailDTO spaceDetailDTO = spaceService.spaceDetail(spaceId, userId);
		return ResponseEntity.ok().body(spaceDetailDTO);
	}

	// 사진 업로드 위치 (해당 이미지를 업로드한 년/월/일)
	private String getUploadPath(){
		return new SimpleDateFormat("yyyy/MM/dd").format(new Date());
	}

	// 스페이스 생성
	@PostMapping(value = "", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
	public ResponseEntity<?> register(@RequestPart SpaceDTO spaceDTO,
									  @RequestPart(required = false) MultipartFile multipartFile,
									  HttpServletRequest request) throws IOException {

		Long userId = getUserId(request);
		LOGGER.info("[register()] param userId : {}", userId);

		SpaceVO spaceVO = new SpaceVO();
		spaceVO.setSpaceName(spaceDTO.getSpaceName());
		spaceVO.setSpacePw(spaceDTO.getSpacePw());
		spaceVO.setSpaceDescription(spaceDTO.getSpaceDescription());

		SpaceUserVO spaceUserVO = new SpaceUserVO();

		if(!multipartFile.isEmpty()){ // 사용자가 이미지를 지정한 경우

			String uploadPath = getUploadPath(); // 2023/11/10
			File uploadFullPath = new File(spaceRootPath, uploadPath); // upload/space/2023/11/10
			if(!uploadFullPath.exists()) {
				uploadFullPath.mkdirs();
			}

			String uuid = UUID.randomUUID().toString();
			String originalFileName = multipartFile.getOriginalFilename();
			String uploadFileName = uuid.toString() + "_" + originalFileName;
			File fullPath = new File(uploadFullPath, uploadFileName); // upload/space/2023/11/10/uuid_space.jpg
			LOGGER.info("[register()] value fullPath : {}", fullPath);

			String uploadUrl = fileService.uploadFile(multipartFile, fullPath.toString());
			LOGGER.info("[register()] value uploadUrl : {}", uploadUrl);

			spaceVO.setSpaceIconName(originalFileName); // space.jpg
			spaceVO.setSpaceIconPath(fullPath.toString()); // upload/space/2023/11/10/uuid_space.jpg
			spaceVO.setSpaceIconUuid(uuid); // uuid
			spaceVO.setSpaceIconSize(multipartFile.getSize()); // ...bytes

		} else { // 기본이미지를 사용한 경우

			Long defaultImg = spaceDTO.getDefaultImg();

			if(defaultImg != null){

				spaceVO.setSpaceIconName(defaultImg + ".jpg"); // 1.jpg
				spaceVO.setSpaceIconUuid("default"); // default
				spaceVO.setSpaceIconPath(defaultRootPath + "/" + defaultImg + ".jpg"); // upload/default/1.jpg
				spaceVO.setSpaceIconSize(0L); // 0L
			} else {

				throw new CustomException(StatusCode.MISSING_IMAGE);
			}
		}

		UUID uuid = UUID.randomUUID();

		spaceVO.setSpaceCode(uuid.toString());
		spaceVO.setSpaceUserTally(1);

		spaceUserVO.setUserId(userId);
		spaceUserVO.setUserAdminYn("1");
		spaceUserVO.setUserNickname("default");
		spaceUserVO.setProfileImgName("default");
		spaceUserVO.setProfileImgPath("default");
		spaceUserVO.setProfileImgUuid("default");
		spaceUserVO.setProfileImgSize(0L);

		Long spaceId = spaceService.register(spaceVO, spaceUserVO);

		return ResponseEntity.ok().body(spaceId);
	}

	// 스페이스 삭제
	@DeleteMapping("")
	public ResponseEntity<StatusCode> remove(@RequestParam Long spaceId){
		spaceService.remove(spaceId);
		return new ResponseEntity<>(StatusCode.OK, HttpStatus.OK);

	}

	// 스페이스 수정
	@PatchMapping("")
	public ResponseEntity<StatusCode> modify(
		@RequestPart SpaceDTO spaceDTO,
		@RequestPart(required = false) MultipartFile multipartFile) throws IOException {

		// 이미지 저장
		if(!multipartFile.isEmpty()){ // 사용자가 이미지를 지정한 경우

			String uploadPath = getUploadPath(); // 2023/11/10
			File uploadFullPath = new File(spaceRootPath, uploadPath); // upload/space/2023/11/10
			if(!uploadFullPath.exists()) {
				uploadFullPath.mkdirs();
			}

			String uuid = UUID.randomUUID().toString();
			String originalFileName = multipartFile.getOriginalFilename();
			String uploadFileName = uuid.toString() + "_" + originalFileName;
			File fullPath = new File(uploadFullPath, uploadFileName); // upload/space/2023/11/10/uuid_space.jpg
			LOGGER.info("[modify()] value fullPath : {}", fullPath);

			String uploadUrl = fileService.uploadFile(multipartFile, fullPath.toString());
			LOGGER.info("[modify()] value uploadUrl : {}", uploadUrl);

			spaceDTO.setSpaceIconName(originalFileName); // space.jpg
			spaceDTO.setSpaceIconPath(fullPath.toString()); // upload/space/2023/11/10/uuid_space.jpg
			spaceDTO.setSpaceIconUuid(uuid); // uuid
			spaceDTO.setSpaceIconSize(multipartFile.getSize()); // ...bytes

		} else { // 기본이미지를 사용한 경우

			if(spaceDTO.getDefaultImg() != null) {

				Long defaultImg = spaceDTO.getDefaultImg();

				spaceDTO.setSpaceIconName(defaultImg + ".jpg"); // 1.jpg
				spaceDTO.setSpaceIconUuid("default"); // default
				spaceDTO.setSpaceIconPath(defaultRootPath + "/" + defaultImg + ".jpg"); // upload/default/1.jpg
				spaceDTO.setSpaceIconSize(0L); // 0L
			} else {

				throw new CustomException(StatusCode.BAD_REQUEST);
			}
		}

		spaceService.modify(spaceDTO);

		return new ResponseEntity<>(StatusCode.OK, HttpStatus.OK);
	}

	// 스페이스 태그 조회
	@GetMapping("/tag")
	public ResponseEntity<?> tagList(@RequestParam Long spaceId){
		List<TagVO> tags = spaceService.tagList(spaceId);
		return ResponseEntity.ok().body(tags);
	}

	// 스페이스 태그 추가
	@PostMapping("/tag")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public ResponseEntity<StatusCode> addTag(@RequestBody TagVO tagVO){
		spaceService.addTag(tagVO);
		return new ResponseEntity<>(StatusCode.OK, HttpStatus.OK);
	}

	// 스페이스 태그 삭제
	@DeleteMapping("/tag")
	public ResponseEntity<StatusCode> removeTag(@RequestParam Long spaceId, @RequestParam Long tagId){
		spaceService.removeTag(tagId);
		return new ResponseEntity<>(StatusCode.OK, HttpStatus.OK);
	}

	// 스페이스 회원 입장 (초대코드 입력)
	@PostMapping("/user")
	public ResponseEntity<?> enterSpace(@RequestBody SpaceVO spaceVO, HttpServletRequest request){
		Long userId = getUserId(request);
		Long spaceId = spaceService.enter(userId, spaceVO);
		return ResponseEntity.ok().body(spaceId);
	}

	// 스페이스 회원 탈퇴
	@DeleteMapping("/user")
	public ResponseEntity<StatusCode> withdrawSpace(@RequestParam Long spaceId, @RequestParam Long userId){
		spaceService.withdrawSpace(spaceId, userId);
		return new ResponseEntity<>(StatusCode.OK, HttpStatus.OK);
	}

	// 스페이스 내 유저 정보 변경
	@PatchMapping(value = "/user", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
	public ResponseEntity<StatusCode> modifyInfo(
		@RequestPart SpaceUserDTO spaceUserDTO,
		@RequestPart(required = false) MultipartFile multipartFile,
		HttpServletRequest request) throws IOException {

		Long userId = getUserId(request);
		log.info("userId: " + userId);

		SpaceUserVO spaceUserVO = new SpaceUserVO();
		spaceUserVO.setUserId(userId);
		spaceUserDTO.setUserId(userId);

		log.info(" ----------- 여기  -----------");
		log.info("spaceUserDTO: " + spaceUserDTO);

		if(spaceUserDTO.getIsAdmin()){

			log.info("방장 변경 들어옴");

			// 방장 변경
			spaceService.modifyStatus(spaceUserDTO.getSpaceId(), spaceUserDTO.getUserId());
		} else {

			if(!multipartFile.isEmpty()){ // 이미지 변경 시

				log.info("이미지 저장 들어옴");

				String uploadPath = getUploadPath(); // 2023/11/10
				File uploadFullPath = new File(spaceRootPath, uploadPath); // upload/profile/2023/11/10
				if(!uploadFullPath.exists()) {
					uploadFullPath.mkdirs();
				}

				String uuid = UUID.randomUUID().toString();
				String originalFileName = multipartFile.getOriginalFilename();
				String uploadFileName = uuid.toString() + "_" + originalFileName;
				File fullPath = new File(uploadFullPath, uploadFileName); // upload/profile/2023/11/10/uuid_profile.jpg
				LOGGER.info("[modifyInfo()] value fullPath : {}", fullPath);

				String uploadUrl = fileService.uploadFile(multipartFile, fullPath.toString());
				LOGGER.info("[modifyInfo()] value uploadUrl : {}", uploadUrl);

				spaceUserVO.setProfileImgName(originalFileName); // profile.jpg
				spaceUserVO.setProfileImgPath(fullPath.toString()); // upload/profile/2023/11/10/uuid_profile.jpg
				spaceUserVO.setProfileImgUuid(uuid); // uuid
				spaceUserVO.setProfileImgSize(multipartFile.getSize()); // ...bytes

			} else { // 기본이미지로 저장

				log.info("기본이미지 저장 들어옴");

				spaceUserVO.setProfileImgName("defaultProfile.png"); // defaultProfile.png
				spaceUserVO.setProfileImgPath(defaultRootPath + "/defaultProfile.png"); // upload/default/defaultProfile.png
				spaceUserVO.setProfileImgUuid("default"); // default
				spaceUserVO.setProfileImgSize(0L); // 0L
			}

			// 유저정보 수정
			spaceUserVO.setSpaceId(spaceUserDTO.getSpaceId());
			spaceUserVO.setUserNickname(spaceUserDTO.getUserNickname());

			log.info("spaceUserVO: " + spaceUserVO);

			spaceService.modifyInfo(spaceUserVO);
		}

		return new ResponseEntity<>(StatusCode.OK, HttpStatus.OK);
	}

	// 게시글 필터 조회
	@GetMapping("/search")
	public ResponseEntity<?> filter(@RequestParam Long spaceId, @RequestParam Long page,
					   @RequestParam(required = false) List<Long> userId,
					   @RequestParam(required = false) List<Long> tagId,
					   @RequestParam(required = false) String keyword,
					   @RequestParam(required = false) String startDate,
					   @RequestParam(required = false) String endDate, HttpServletRequest request){
//		Long id = getUserId(request);
		Long id = 1L;
		Map<String, Object> param = new HashMap<>();
		List<LocalDate> dateList = new ArrayList<>();
		int amount = 10;
		param.put("spaceId", spaceId);
		param.put("page", page);
		param.put("writers", userId);
		param.put("tags", tagId);
		param.put("keyword", keyword);

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/d");
		if(startDate != null && endDate !=null){
			LocalDate start = LocalDate.parse(startDate, formatter);
			LocalDate end = LocalDate.parse(endDate, formatter);

			Long numOfDaysBetween = ChronoUnit.DAYS.between(start, end);
			dateList = IntStream.iterate(0, i -> i + 1)
			.limit(numOfDaysBetween)
			.mapToObj(i -> start.plusDays(i))
			.collect(Collectors.toList());
		} else if (startDate != null) {
			dateList.add(LocalDate.parse(startDate, formatter));
		} else if(endDate != null){
			dateList.add(LocalDate.parse(endDate, formatter));
		}

		param.put("dateList", dateList);
		param.put("amount", amount);

		return ResponseEntity.ok().body(postService.filter(param, id));
	}
}
