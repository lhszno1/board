package com.solo.board.dao;

import com.solo.board.dto.MemberDto;

public interface MemberDao {

	//1. 아이디 중복 체크용 
	public int idCheck(String id);
	//2. 회원가입 처리
	public void memberInsert(MemberDto member);
	//3. 로그인(비밀번호 구하기)
	public String pwdSelect(String id);
	//4. 회원정보 구하기
	public MemberDto memberSelect(String id);
	//5. 회원 포인트 수정
	public void mPointUpdate(String id);
}
