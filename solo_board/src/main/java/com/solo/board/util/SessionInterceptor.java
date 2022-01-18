package com.solo.board.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import lombok.extern.java.Log;
@Log
public class SessionInterceptor extends HandlerInterceptorAdapter {
	@Autowired
	HttpSession session;
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		//세션에 로그인 정보가 없으면 첫페이지 강제이동
		if(session.getAttribute("mb") == null) {
			log.info("preHandle() - 인터셉트");
			response.sendRedirect("/");
			return false;
		}
		return true;
	}
	//로그아웃 후 뒤로 가기 막기 메소드
	//브라우저에 캐쉬(저장)된 페이지를 제거하도록 명령하는 메소드
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		log.info("postHandle() - 뒤로가기막기");
		//현재 웹 프로토콜 버전 1.0과 1.1이 혼용되고 있음
		if(request.getProtocol().equals("HTTP/1.1")) {
			response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		}else {
			response.setHeader("Pragma", "no-cache");
		}
		response.setDateHeader("Expries", 0);
	}
}
