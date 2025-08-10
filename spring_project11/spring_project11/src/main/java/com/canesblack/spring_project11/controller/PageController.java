package com.canesblack.spring_project11.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

//@Component 한마디로 스프링빈으로 등록하기 위한 라벨링 작업
@Controller
public class PageController {
	//@PostMapping()
	//@PutMapping()
	//@DeleteMapping()
	// /=>localhost:8080
	@GetMapping("/")
	public String returnHome() {
		return "index";
	}
	//페이지를 조회 및 이동할때 @GetMapping()을 써서 이동합니다.
	@GetMapping("/register")
	public String registerPage() {
		return "register/index";
	}
	
	// =>localhost:8080/loginpage
	@GetMapping("/loginPage")
	public String loginPage() {
		return "login/index";
	}
	
}
