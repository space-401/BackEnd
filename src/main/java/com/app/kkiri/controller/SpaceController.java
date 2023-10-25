package com.app.kkiri.controller;

import com.app.kkiri.domain.dto.SpaceDTO;
import com.app.kkiri.domain.dto.SpaceDetailDTO;
import com.app.kkiri.domain.dto.SpaceListDTO;
import com.app.kkiri.domain.dto.SpaceUserDTO;
import com.app.kkiri.domain.vo.*;
import com.app.kkiri.exceptions.CustomException;
import com.app.kkiri.exceptions.StatusCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.app.kkiri.service.SpaceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/space/*")
@Slf4j
public class SpaceController {
	private final SpaceService spaceService;

	// 목록 조회
	@GetMapping("/list")
	public ResponseEntity<?> list(){
//		userId 수정
		Long userId = 1L;
		List<SpaceListDTO> spaceList = spaceService.list(userId);
		return ResponseEntity.ok().body(spaceList);
	};

	// 스페이스 상세 조회
	@GetMapping("")
	public ResponseEntity<?> spaceDetail(@RequestParam Long spaceId){
		Long userId = 1L;
		SpaceDetailDTO spaceDetailDTO = spaceService.spaceDetail(spaceId, userId);
		return ResponseEntity.ok().body(spaceDetailDTO);
	};

	// 사진 업로드 위치 (해당 이미지를 업로드한 년/월/일)
	private String getUploadPath(){
		return new SimpleDateFormat("yyyy/MM/dd").format(new Date());
	};

	// 스페이스 생성
	@PostMapping(value = "", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
	public ResponseEntity<?> register(@RequestPart SpaceDTO spaceDTO, @RequestPart(required = false) MultipartFile imgUrl) throws IOException {
		SpaceVO spaceVO = new SpaceVO();
		spaceVO.setSpaceName(spaceDTO.getSpaceName());
		spaceVO.setSpacePw(spaceDTO.getSpacePw());
		spaceVO.setSpaceDescription(spaceDTO.getSpaceDescription());

		log.info("spaceName: " + spaceDTO.getSpaceName());
		log.info("spaceName: " + spaceDTO.getSpaceDescription());
		log.info("spaceName: " + spaceDTO.getSpacePw());

		Long defaultImg = spaceDTO.getDefaultImg();

		SpaceUserVO spaceUserVO = new SpaceUserVO();
		Long userId = 1L;

		UUID uuid = UUID.randomUUID();
		
		if(!imgUrl.isEmpty()){
			// 이미지를 업로드했을 경우
			String rootPath = "/home/ec2-user/upload/space";
			String uploadPath = getUploadPath();
			String uploadFileName = "";
			String spaceIconPath = "";

			File uploadFullPath = new File(rootPath, uploadPath);
			if(!uploadFullPath.exists()){uploadFullPath.mkdirs();}

			String fileName = imgUrl.getOriginalFilename();
			uploadFileName = uuid.toString() + "_" + fileName;

			File fullPath = new File(uploadFullPath, uploadFileName);
			imgUrl.transferTo(fullPath);

			spaceIconPath = uploadPath + "/" + uploadFileName;

			spaceVO.setSpaceIconUuid(uploadFileName);
			spaceVO.setSpaceIconPath(spaceIconPath);
			spaceVO.setSpaceIconName(fileName);
			spaceVO.setSpaceIconSize(imgUrl.getSize());


		} else{
			// 기본이미지를 사용한 경우
			if(defaultImg != null){
				spaceVO.setSpaceIconUuid("default");
				spaceVO.setSpaceIconPath("/home/ec2-user/upload/default/" + defaultImg +".jpg");
				spaceVO.setSpaceIconName(defaultImg + ".jpg");
				spaceVO.setSpaceIconSize(0L);
			} else {
				throw new CustomException(StatusCode.BAD_REQUEST);
			}
		}
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
	};

	// 스페이스 삭제
	@DeleteMapping("")
	public ResponseEntity<StatusCode> remove(@RequestParam Long spaceId){
		spaceService.remove(spaceId);
		return new ResponseEntity<>(StatusCode.OK, HttpStatus.OK);

	};

	// 스페이스 수정
	@PatchMapping("")
	public ResponseEntity<StatusCode> modify(@RequestPart SpaceDTO spaceDTO, @RequestPart(required = false) MultipartFile imgUrl) throws IOException{

		log.info("------------------- 여기 ---------------");
		log.info("spaceDTO: " + spaceDTO);
		log.info("imgUrl: " + imgUrl);
		log.info("dd");

		// 이미지 저장
		if(imgUrl != null){
			// 이미지를 업로드했을 경우
			String rootPath = "/home/ec2-user/upload/space";
			String uploadPath = getUploadPath();
			String uploadFileName = "";
			String spaceIconPath = "";

			File uploadFullPath = new File(rootPath, uploadPath);
			if(!uploadFullPath.exists()){uploadFullPath.mkdirs();}

			UUID uuid = UUID.randomUUID();
			String fileName = imgUrl.getOriginalFilename();
			uploadFileName = uuid.toString() + "_" + fileName;

			File fullPath = new File(uploadFullPath, uploadFileName);
			imgUrl.transferTo(fullPath);
			log.info("uploadPath: " + uploadPath);

			spaceIconPath = uploadPath + "/" + uploadFileName;

			spaceDTO.setSpaceIconUuid(uploadFileName);
			spaceDTO.setSpaceIconPath(spaceIconPath);
			spaceDTO.setSpaceIconName(fileName);
			spaceDTO.setSpaceIconSize(imgUrl.getSize());

			log.info("spaceDTO: " + spaceDTO);
		} else {
			// 기본이미지를 사용한 경우
			if(spaceDTO.getDefaultImg() != null){
				spaceDTO.setSpaceIconUuid("default");
				spaceDTO.setSpaceIconPath("/home/ec2-user/upload/default/" + spaceDTO.getDefaultImg() +".jpg");
				spaceDTO.setSpaceIconName(spaceDTO.getDefaultImg() + ".jpg");
				spaceDTO.setSpaceIconSize(0L);
			} else {
				throw new CustomException(StatusCode.BAD_REQUEST);
			}
		}

		spaceService.modify(spaceDTO);
		return new ResponseEntity<>(StatusCode.OK, HttpStatus.OK);
	};

	// 게시글 필터 조회
//	?spaceId={spaceId}&userId={userId}&tagId={tagId}&keyword={keyword}&startDate={startDate}&endDate={endDate}
	@GetMapping("/search")
	public void filter(){};

	// 스페이스 태그 조회
	@GetMapping("/tag")
	public ResponseEntity<?> tagList(@RequestParam Long spaceId){
		List<TagVO> tags = spaceService.tagList(spaceId);
		return ResponseEntity.ok().body(tags);
	};

	// 스페이스 태그 추가
	@PostMapping("/tag")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public ResponseEntity<StatusCode> addTag(@RequestBody TagVO tagVO){
		spaceService.addTag(tagVO);
		return new ResponseEntity<>(StatusCode.OK, HttpStatus.OK);
	};

	// 스페이스 태그 삭제
	@DeleteMapping("/tag")
	public ResponseEntity<StatusCode> removeTag(@RequestParam Long spaceId, @RequestParam Long tagId){
		spaceService.removeTag(tagId);
		return new ResponseEntity<>(StatusCode.OK, HttpStatus.OK);
	};

	// 스페이스 회원 입장 (초대코드 입력)
	@PostMapping("/user")
	public ResponseEntity<?> enterSpace(@RequestBody SpaceVO spaceVO){
		Long userId = 1L;
		Long spaceId = spaceService.enter(userId, spaceVO);
		return ResponseEntity.ok().body(spaceId);
	};

	// 스페이스 회원 탈퇴
	@DeleteMapping("/user")
	public ResponseEntity<StatusCode> withdrawSpace(@RequestParam Long spaceId, @RequestParam Long userId){
		spaceService.withdrawSpace(spaceId, userId);
		return new ResponseEntity<>(StatusCode.OK, HttpStatus.OK);
	};

	// 스페이스 내 유저 정보 변경
	@PatchMapping(value = "/user", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
	public ResponseEntity<StatusCode> modifyInfo(@RequestPart SpaceUserDTO spaceUserDTO, @RequestPart(required = false) MultipartFile image) throws IOException{
		Long userId = 1L;
		SpaceUserVO spaceUserVO = new SpaceUserVO();
		spaceUserVO.setUserId(userId);

		log.info(" ----------- 여기  -----------");
		log.info("spaceUserDTO: " + spaceUserDTO);

		if(spaceUserDTO.getIsAdmin()){
			log.info("방장 변경 들어옴");
			// 방장 변경
			spaceService.modifyStatus(spaceUserDTO.getSpaceId(), userId);
		} else {
			log.info("이미지 저장 들어옴");
//			이미지 저장
			String rootPath = "/home/ec2-user/upload/profile";
			String uploadPath = getUploadPath();
			String uploadFileName = "";
			String profilePath = "";
			MultipartFile file = image;

			File uploadFullPath = new File(rootPath, uploadPath);
			if(!uploadFullPath.exists()){uploadFullPath.mkdirs();}

			UUID uuid = UUID.randomUUID();
			String fileName = file.getOriginalFilename();
			uploadFileName = uuid.toString() + "_" + fileName;

			File fullPath = new File(uploadFullPath, uploadFileName);
			file.transferTo(fullPath);
			log.info("uploadPath: " + uploadPath);

			profilePath = uploadPath + "/" + uploadFileName;

			// 유저 정보 수정
			spaceUserVO.setSpaceId(spaceUserDTO.getSpaceId());
			spaceUserVO.setUserNickname(spaceUserDTO.getUserNickname());
			spaceUserVO.setProfileImgPath(profilePath);
			spaceUserVO.setProfileImgName(uploadFileName);
			spaceUserVO.setProfileImgUuid(fileName);
			spaceUserVO.setProfileImgSize(file.getSize());

			log.info("spaceUserVO: " + spaceUserVO);

			spaceService.modifyInfo(spaceUserVO);
		}

		return new ResponseEntity<>(StatusCode.OK, HttpStatus.OK);
	};
}
