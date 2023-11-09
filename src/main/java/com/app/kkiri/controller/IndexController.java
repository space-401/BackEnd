package com.app.kkiri.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

@RestController
public class IndexController {
	@GetMapping("/")
	public ResponseEntity<Map<String, Object>> index() {

		Gson gson = new Gson();

		Map<String, Object> map = new HashMap<>();
		map.put("message", "랜딩 페이지를 요청합니다");

		return ResponseEntity.status(HttpStatus.OK.value()).body(map);
	}
}
