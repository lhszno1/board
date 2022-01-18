package com.solo.board;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.solo.board.dto.MemberDto;
import com.solo.board.service.MemberService;



@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	@Autowired
	private MemberService mServ;
	
	//1. 메인페이지
	@GetMapping("/")
	public String home() {
		logger.info("home()");
	
		return "home";
	}
	//2. 회원가입페이지 이동
	@GetMapping("joinFrm")
	public String joinFrm() {
		logger.info("joinFrm()");
		return "joinFrm";
	}
	//3.회원가입처리
	@PostMapping("memInsert")//joinFrm form action
	public String memberInsert(MemberDto member, RedirectAttributes rttr) {
		logger.info("memInsert()");
		String view = mServ.memberInsert(member, rttr);
		return view;
	}
	//4.아이디 중복 체크		ajax url
	@GetMapping(value = "idCheck", produces = "application/text; charset= UTF-8")
	@ResponseBody
	public String idCheck(String mid) {
		logger.info("idCheck()-mid : "+mid);
		//이 후 해당 아이디로 DB를 검색하는 서비스와 Dao를 활용
		String res = mServ.idCheck(mid);
		return res;
	}
	//5.로그인 화면 이동 a tag getmapping
	@GetMapping("loginFrm")
	public String loginFrm() {
		logger.info("loginFrm()");
		return "loginFrm";
	}
	//6. 로그인 처리
	@PostMapping("loginProc")
	public String loginProc(MemberDto member, RedirectAttributes rttr) {
		logger.info("loginProc()");
		String view = mServ.loginProc(member, rttr);
		return view;
	}
	//7. 로그아웃 이동
	@GetMapping("logout")
	public String logout() {
		logger.info("logout()");
		String view = mServ.logout();
		return view;
	}
	
}
