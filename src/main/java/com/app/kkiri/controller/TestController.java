package com.app.kkiri.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.kkiri.security.Response;
import com.app.kkiri.service.FileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TestController {

	private final FileService fileService;

	@GetMapping("/test")
	public String test() {
		return "test";
	}

	@GetMapping("/test/file")
	public String testFile(String keyName) throws Exception {
		return fileService.getS3ObjectURL(keyName);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Response> exceptionHandler(Exception e) {
		Response response = new Response(e.getMessage());

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	}

}
