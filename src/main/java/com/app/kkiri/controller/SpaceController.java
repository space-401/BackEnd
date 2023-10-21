package com.app.kkiri.controller;

import com.app.kkiri.domain.vo.*;
import com.app.kkiri.exceptions.CustomException;
import com.app.kkiri.exceptions.StatusCode;
import com.app.kkiri.service.DefaultImgService;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
	private final DefaultImgService defaultImgService;

	// 목록 조회
	@GetMapping("/list")
	public ResponseEntity<?> list(){
//		userId 수정
		Long userId = 1L;
		List<SpaceListDTO> spaceList = spaceService.list(userId);
		log.info("controller spaceList: " + spaceList);
		return ResponseEntity.ok().body(spaceList);
	};

	// 스페이스 상세 조회
	@GetMapping("/")
	public ResponseEntity<?> spaceDetail(@RequestParam Long spaceId){
//		userId 수정
		Long userId = 1L;
		SpaceDetailDTO spaceDetailDTO = spaceService.spaceDetail(spaceId, userId);
		return ResponseEntity.ok().body(spaceDetailDTO);
	};

	// 사진 업로드 위치 (해당 이미지를 업로드한 년/월/일)
	private String getUploadPath(){
		return new SimpleDateFormat("yyyy/MM/dd").format(new Date());
	};

	// 스페이스 생성
	@PostMapping(value = "/", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
	public ResponseEntity<?> register(@RequestPart SpaceVO spaceVO, @RequestPart MultipartFile imgUrl, @RequestPart(required = false) Long defaultImg) throws IOException {
		SpaceUserVO spaceUserVO = new SpaceUserVO();
		Long userId = 1L;

		log.info("------------------ 여기 ------------------");

		if(imgUrl != null){
			// 이미지를 업로드했을 경우
			String rootPath = "/Users/son/Documents/upload/space";
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

			spaceVO.setSpaceIconUuid(uploadFileName);
			spaceVO.setSpaceIconPath(spaceIconPath);
			spaceVO.setSpaceIconName(fileName);
			spaceVO.setSpaceIconSize(imgUrl.getSize());

		} else if(imgUrl == null){
			// 기본이미지를 사용한 경우
			if(defaultImg != null){
				DefaultImgVO defaultImgVO = defaultImgService.defaultImgData(defaultImg);
				spaceVO.setSpaceIconUuid(defaultImgVO.getDefaultImgUuid());
				spaceVO.setSpaceIconPath(defaultImgVO.getDefaultImgPath());
				spaceVO.setSpaceIconName(defaultImgVO.getDefaultImgName());
				spaceVO.setSpaceIconSize(defaultImgVO.getDefaultImgSize());
			} else {
				throw new CustomException(StatusCode.BAD_REQUEST);
			}
		}
		spaceUserVO.setUserId(userId);
		Long spaceId = spaceService.register(spaceVO, spaceUserVO);
		return ResponseEntity.ok().body(spaceId);
	};

	// 스페이스 삭제
	@DeleteMapping("/")
	public ResponseEntity<StatusCode> remove(@RequestParam Long spaceId){
		spaceService.remove(spaceId);
		return new ResponseEntity<>(StatusCode.OK, HttpStatus.OK);

	};

	// 스페이스 수정
	@PatchMapping("/")
	public ResponseEntity<StatusCode> modify(@RequestPart SpaceVO spaceVO, @RequestPart MultipartFile imgUrl){
		spaceService.modify(spaceVO);
		// 이미지 저장
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
	public ResponseEntity<StatusCode> modifyInfo(@RequestPart SpaceUserDTO spaceUserDTO) throws IOException{
		SpaceUserVO spaceUserVO = new SpaceUserVO();
		if(spaceUserDTO.isAdmin()){
			// 방장 변경
			spaceService.modifyStatus(spaceUserDTO.getSpaceId(), spaceUserDTO.getUserId());
		} else {
//			이미지 저장
			String rootPath = "/Users/son/Documents/upload/profile";
			String uploadPath = getUploadPath();
			String uploadFileName = "";
			String profilePath = "";
			MultipartFile file = spaceUserDTO.getImage();

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

			spaceService.modifyInfo(spaceUserVO);
		}
		return new ResponseEntity<>(StatusCode.OK, HttpStatus.OK);
	};
}
