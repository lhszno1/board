package com.solo.board.dao;

import java.util.List;
import java.util.Map;

import com.solo.board.dto.BfileDto;
import com.solo.board.dto.BoardDto;
import com.solo.board.dto.ReplyDto;

public interface BoardDao {
	//1. 게시글 목록 가져오기(Board)
	public List<BoardDto> getList(Map<String, Integer> pmap);//페이지 번호 변경하게 하려면 Map사용
	//2. 전체 글 개수 구하기
	public int getboardCnt();
	//3. 게시글 저장
	public void boardInsert(BoardDto board);
	//4. 파일 저장
	public void fileInsert(Map<String, String> fmap);
	//5. 글 내용 가져오기
	public BoardDto getContents(Integer bnum);//dao에 bnum 번호가 필요하다
	//6. 파일 목록 가져오기
	public List<BfileDto> getBfList(Integer bnum);
	//7. 댓글 목록 가져오기
	public List<ReplyDto> getReList(Integer bnum);
	//8. 댓글 저장
	public void replyInsert(ReplyDto reply);
	//9. 조회수 증가
	public void viewUpdate(Integer bnum);
	//10. 게시글 업데이트
	public boolean boardUpdate(BoardDto board);
	//11. 개별 파일 삭제(게시물 수정시)
	public boolean fileDelete(String sysName);
	//12-1. 게시글의 댓글 삭제
	public boolean replyDelete(Integer bnum);
	//12-2. 게시글의 파일 삭제(같은 게시물의 모든 파일 삭제)
	public boolean filesListDelete(Integer bnum);
	//12-3. 게시글 삭제
	public boolean boardDelete(Integer bnum);
}
