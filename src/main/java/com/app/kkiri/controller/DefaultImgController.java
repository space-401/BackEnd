package com.app.kkiri.controller;

import com.app.kkiri.domain.vo.DefaultImgVO;
import com.app.kkiri.domain.vo.SpaceListDTO;
import com.app.kkiri.exceptions.StatusCode;
import com.app.kkiri.service.DefaultImgService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/default/*")
@Slf4j
public class DefaultImgController {
    private final DefaultImgService defaultImgService;

    private String getUploadPath(){
        return new SimpleDateFormat("yyyy/MM/dd").format(new Date());
    };

    // 목록 조회
    @PostMapping(value = "/", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> upload(@RequestPart MultipartFile upload, @RequestPart Long order) throws IOException {
        String rootPath = "/Users/son/Documents/upload/default";
        String uploadPath = getUploadPath();
        String uploadFileName = "";
        String defaultImgPath = "";

        File uploadFullPath = new File(rootPath, uploadPath);
        if(!uploadFullPath.exists()){uploadFullPath.mkdirs();}

        UUID uuid = UUID.randomUUID();
        String fileName = upload.getOriginalFilename();
        uploadFileName = uuid.toString() + "_" + fileName;

        File fullPath = new File(uploadFullPath, uploadFileName);
        upload.transferTo(fullPath);
        log.info("uploadPath: " + uploadPath);

        defaultImgPath = uploadPath + "/" + uploadFileName;

        DefaultImgVO defaultImgVO = new DefaultImgVO();
        defaultImgVO.setDefaultImgName(fileName);
        defaultImgVO.setDefaultImgPath(defaultImgPath);
        defaultImgVO.setDefaultImgSize(upload.getSize());
        defaultImgVO.setDefaultImgUuid(uploadFileName);
        defaultImgVO.setDefaultImgOrder(order);
        defaultImgService.register(defaultImgVO);

        return new ResponseEntity<>(StatusCode.OK, HttpStatus.OK);
    };

    public ResponseEntity<?> delete(@RequestBody Long defaultImgId){
        defaultImgService.remove(defaultImgId);
        return new ResponseEntity<>(StatusCode.OK, HttpStatus.OK);
    };
}
