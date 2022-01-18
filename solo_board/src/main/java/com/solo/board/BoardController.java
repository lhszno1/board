package com.solo.board;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.solo.board.BoardController;
import com.solo.board.dto.BfileDto;
import com.solo.board.dto.ReplyDto;
import com.solo.board.service.BoardService;

import lombok.extern.java.Log;

@Controller
@Log
public class BoardController {
	private static final Logger logger = LoggerFactory.getLogger(BoardController.class);
	@Autowired
	private BoardService bServ;
	private ModelAndView mv;
	
	//로그인 이후 화면
	//list면 페이지 번호가 들어와야한다
	//1. 게시글 목록보기 이동 (로그인후 첫화면)
	@GetMapping("list")
	public ModelAndView boardList(Integer pageNum) {
		logger.info("boardList()");
		
		mv = bServ.getBoardList(pageNum);
		return mv;
	}
	//2. 글쓰기 페이지 이동
	@GetMapping("writeFrm")
	public String writeFrm() {
		logger.info("writeFrm()");
		return "writeFrm";
	}
	//3. 게시물 등록
	//멀티파트 데이터를 처리할 경우 첫번째 매개변수는
	//multipartServletRequest여야 한다
	@PostMapping("boardWrite")
	public String boardWrite(MultipartHttpServletRequest multi, RedirectAttributes rttr) {
		logger.info("boardWrite()");
		String view = bServ.boardInsert(multi, rttr);
		return view;
	}
	//4. 글 상세보기페이지 이동
	@GetMapping("contents")
	public ModelAndView boardContents(Integer bnum) {
		logger.info("boardContents() - bnum" + bnum);
		//서비스에서 mv내용을 만들어서 보내주도록 처리
		mv = bServ.getContents(bnum);
		return mv;
	}
	//5. 파일 다운로드
	//디스패쳐 서블릿으로 가서 void
	@GetMapping("download")
	public void fileDownload(BfileDto bfile, HttpServletResponse res) {
		logger.info("fileDownload() - oriname : " + bfile.getBf_oriname());
		bServ.fileDownload(bfile, res);		
	}
	//6. 댓글입력처리 및 저장
	@PostMapping(value = "replyIns", produces = "application/json; charset = UTF-8")
	@ResponseBody
	public Map<String, List<ReplyDto>> replyInsert(ReplyDto reply){
		logger.info("replyInsert()");
		Map<String, List<ReplyDto>> rmap = bServ.replyInsert(reply);
		return rmap;
	}
	//7. 수정페이지 이동
	@GetMapping("updateFrm")
	public ModelAndView updateFrm(int bnum, RedirectAttributes rttr) {
		logger.info("updateFrm() - bnum : " + bnum);
		mv = bServ.updateFrm(bnum, rttr);
		return mv;
	}
	//8. 게시물 수정 처리
	@PostMapping("boardUpdate")
	public String boardUpdate(MultipartHttpServletRequest multi, RedirectAttributes rttr) {
		logger.info("boardUpdate()");
		String view = bServ.boardUpdate(multi, rttr);
		return view;
	}
	//9.개별파일삭제(게시물수정시)delfile
	@PostMapping(value = "delfile", produces = "application/json; charset=utf-8")
	@ResponseBody
	public Map<String, List<BfileDto>> delFile(String sysname, int bnum){
		logger.info("delFile()");
		Map<String, List<BfileDto>> rMap = null;
		rMap = bServ.fileDelete(sysname, bnum);
		return rMap;
	}
	//10.게시글 삭제
	@GetMapping("delete")
	public String boardDelete(int bnum, RedirectAttributes rttr) {
		logger.info("boardDelete() - bnum : " + bnum);
		String view = bServ.boardDelete(bnum, rttr);
		
		return view;
	}
	
}















































