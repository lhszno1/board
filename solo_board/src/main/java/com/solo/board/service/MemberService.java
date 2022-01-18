package com.solo.board.service;


import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.solo.board.dao.MemberDao;
import com.solo.board.dto.MemberDto;

@Service
public class MemberService {
	@Autowired
	private MemberDao mDao;
	@Autowired
	private HttpSession session;
	private ModelAndView mv;
	
	
	//1. 아이디 증복 처리
	public String idCheck(String id) {
		String res = null;
		int cnt = mDao.idCheck(id);
		if(cnt == 0) {
			res = "ok";
		}else {
			res = "fail";
		}		
		return res;
	}
	//2.회원가입처리 성공 후 첫페이지이동 성공/실패시 메세지 전송
	@Transactional
	public String memberInsert(MemberDto member, RedirectAttributes rttr) {
		String view = null;
		String msg = null;
		BCryptPasswordEncoder pwdEncoder = new BCryptPasswordEncoder();
		String encPwd = pwdEncoder.encode(member.getM_pwd());
		member.setM_pwd(encPwd);
		try {
			mDao.memberInsert(member);
			view = "redirect:/";
			msg = "회원 가입 성공";
		}catch (Exception e) {
			//e.printStackTrace();
			view = "redirect:joinFrm";
			msg = "회원 가입 실패";
		}
		rttr.addFlashAttribute("msg", msg);
		
		return view;
	}
	//3. 로그인처리(비밀번호 구하기)
	public String loginProc(MemberDto member, RedirectAttributes rttr) {
		String view = null;
		String msg = null;
		
		String encPwd = mDao.pwdSelect(member.getM_id());
		if(encPwd != null) {
			BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
			if(enc.matches(member.getM_pwd(), encPwd)) {
				member = mDao.memberSelect(member.getM_id());
				session.setAttribute("mb", member);
				view = "redirect:list";
			}else {
				view = "redirect:loginFrm";
				msg = "비밀번호가 다릅니다.";
			}
		}else {
			view = "redirect:loginFrm";
			msg = "아이디가 다릅니다.";
		}
		rttr.addFlashAttribute("msg", msg);
		return view;
	}
	//4. 로그아웃 처리
	public String logout() {
		String view = "redirect:/";
		session.invalidate();
		return view;
	}
}




























