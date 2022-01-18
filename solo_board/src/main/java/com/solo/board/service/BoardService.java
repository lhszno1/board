package com.solo.board.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.solo.board.dao.BoardDao;
import com.solo.board.dao.MemberDao;
import com.solo.board.dto.BfileDto;
import com.solo.board.dto.BoardDto;
import com.solo.board.dto.MemberDto;
import com.solo.board.dto.ReplyDto;
import com.solo.board.util.PagingUtil;

import lombok.extern.java.Log;

@Service
@Log
public class BoardService {
	@Autowired
	private BoardDao bDao;
	@Autowired
	private MemberDao mDao;//게시글 작성 시 사용자의 point 값 증가를 위해 필요
	@Autowired
	private HttpSession session;//point 증가 후 회원정보를 변경하기 위해 필요
	private ModelAndView mv;
	private int listCnt = 10;//페이지 당 게시글 개수
	//1.화면에 목록 출력
	public ModelAndView getBoardList(Integer pageNum) {
		mv = new ModelAndView();
		//null 또는 페이지 번호가 pageNum으로 넘어옴
		int num = (pageNum == null) ? 1 : pageNum;
		//로그인 후에는 null이 넘어옴.
		//게시글 목록 가져오기
		Map<String, Integer> pmap = new HashMap<String, Integer>();
		pmap.put("num", num);
		pmap.put("lcnt", listCnt);
		//listCnt는 게시글 목록수 차후 view(jsp)에서 
		//페이지 당 글 개수를 입력받아서 설정하는 부분을 처리하여 listCnt대신 사용
		List<BoardDto> bList = bDao.getList(pmap);
		//화면에 전송작업
		mv.addObject("bList", bList);
		//페이징 처리
		String pageHtml = getpaging(num);
		mv.addObject("paging", pageHtml);
		//세션에 페이지번호 저장
		//글작성 화면 글내용 상세보기 화면 등에서 다시 목록으로
		//돌아갈때 보고 있던 페이지가 나오도록 하기 위해
		session.setAttribute("pageNum", num);
		//jsp 파일 이름 지정
		mv.setViewName("boardList");
		
		return mv;
	}
	//2.페이징 메소드
	private String getpaging(int num) {
		String pageHtml = null;
		//전체 글개수 구하기DAO
		int maxNum = bDao.getboardCnt();
		//한 페이지에 보여질 페이지 번호 개수
		int pageCnt = 2;
		String listName = "list";
		PagingUtil paging = new PagingUtil(maxNum, num, listCnt, pageCnt, listName);
		pageHtml = paging.makePaging();
		return pageHtml;
	}
	//3.게시물 등록
	@Transactional
	public String boardInsert(MultipartHttpServletRequest multi, RedirectAttributes rttr) {
		String view = null;
		String msg = null;
		//multi에서 테이터를 추출(게시글의 텍스트 부분)
		String id = multi.getParameter("bid");
		String title = multi.getParameter("btitle");
		String content = multi.getParameter("bcontents");
		String check = multi.getParameter("fileCheck");
		//textarea는 실제 데이터 앞뒤에 공백이 발생하는 경우가 종종있음
		content = content.trim();//앞뒤 공백제거
		//게시글 내용을 dto 담아서 dao로 넘김
		BoardDto board = new BoardDto();
		board.setBid(id);
		board.setBtitle(title);
		board.setBcontents(content);
		try {
			//insert는 필수
			//게시글을 DB에 저장
			bDao.boardInsert(board);//이문장 실행 후 bnum에 게시글 번호 저장
			//업로드 파일이 있을 경우 파일 저장 및 DB에 저장
			if(check.equals("1")) {
				fileUpload(multi, board.getBnum());
			}
			//회원의 포인트 증가 처리 및 session데이터 변경
			//현재 로그인 한 회원의 포인트가 100보다 작을때
			//글 작성에 따라 포인트 점수를 증가
			MemberDto member = (MemberDto) session.getAttribute("mb");
			if(member.getM_point() < 100) {
				mDao.mPointUpdate(id);
				member = mDao.memberSelect(id);
				session.setAttribute("mb", member);//중요 세션에 다시 집어 넣기
			}
			view = "redirect:list";//목록의 첫페이지로
			msg = "글 작성 성공";
		}catch (Exception e) {
			//e.printStackTrace();
			view = "redirect:writeFrm";//목록의 첫페이지로
			msg = "글 작성 실패";
		}
		rttr.addFlashAttribute("msg", msg);
		
		return view;
	}
	//4. 업로드 파일이 있을 경우 파일 저장 및 DB에 저장
	private void fileUpload(MultipartHttpServletRequest multi, int bnum) throws Exception {
		//multi는 업로드한 파일을 포함하고 있음
		//업로드한 파일과 게시물을 bnum으로 연결
		//실제 파일이 서버에 저장될 경로
		String realPath = session.getServletContext().getRealPath("/");
		//파일 저장 경로 추가
		realPath += "resources/upload/";
		log.info(realPath);
		//폴더 존재하는지 확인해서 없을 경우 생성
		File folder = new File(realPath);
		if(folder.isDirectory() == false) {
			folder.mkdir();//폴더가 없을 경우 생성하는 메소드
		}
		//파일 정보를 DB에 저장(bnum, oriname, sysname)
		Map<String, String> fmap = new HashMap<String, String>();
		fmap.put("bnum", String.valueOf(bnum));
		//oriname 원래 이름을 구하고, 각파일별로 sysname을 만들어서 넣기
		//multi에서 파일태그의 name 값 꺼내기
		Iterator<String> files = multi.getFileNames();
		
		while(files.hasNext()) {//hasnext하나씩 꺼내온다
			String fn = files.next();//file태그의 name 값 꺼내기
			//하나의 file input태그에는 여러 파일이 업로드 될수 있다.
			List<MultipartFile> fList = multi.getFiles(fn);
			//각각의 파일을 처리
			for(int i = 0; i < fList.size(); i++) {
				MultipartFile mf = fList.get(i);
				//파일 이름 구하기
				String orname = mf.getOriginalFilename();
				//변경할 이름 만들기
				String syname = System.currentTimeMillis() + orname.substring(orname.lastIndexOf("."));
				//파일명들을 map에 저장
				fmap.put("oriName", orname);
				fmap.put("sysName", syname);
				//폴더에 파일저장
				File ff = new File(realPath + syname);
				mf.transferTo(ff);
				//DB에 파일 정보 저장
				bDao.fileInsert(fmap);
			}
		}		
	}
	//5. 게시물 구해오기
	public ModelAndView getContents(Integer bnum) {
		mv = new ModelAndView();
		
		//조회수 1 업데이트(연구과제)
		bDao.viewUpdate(bnum);
		//글 내용 가져오기
		BoardDto board = bDao.getContents(bnum);
		//파일 목록 가져오기
		List<BfileDto> bfList = bDao.getBfList(bnum);
		//댓글목록 가져오기
		List<ReplyDto> rList = bDao.getReList(bnum);
		//가져온 데이터 mv에 담기
		mv.addObject("board", board);
		mv.addObject("fList", bfList);
		mv.addObject("rList", rList);
		//보여질 페이지(jsp) 이름 저장
		mv.setViewName("boardContents");
		return mv;
	}
	//6. 데이터 다운로드 처리
	public void fileDownload(BfileDto bfile, HttpServletResponse res) {
		//이 메소드가 직접 사용자의 컴퓨터로 파일을 전송
		//이 때 response 객체를 사용
		//실제 파일 저장 경로(upload folder)에서 파일을 가져오기
		String realPath = session.getServletContext().getRealPath("/");
		realPath += "resources/upload/";
		//파일명을 원래 이름으로 변경
		String oName = bfile.getBf_oriname();
		realPath += bfile.getBf_sysname();
		//서버폴더에서 파일을 가져오는 통로 선언
		InputStream is = null;
		//사용자 컴퓨터로 파일을 보내는 통로 선언
		OutputStream os = null;
		try {
			//파일명 인코딩(한글깨짐 방지)
			String dfName = URLEncoder.encode(oName, "UTF-8");
			//메모리에 파일 객체만들고 서버 하드디스크 업로드 파일이 인풋스트링에 연결되서 작업
			File file = new File(realPath);
			is = new FileInputStream(file);
			//인터넷을 통해 전달하기 위한 설정
			//이문장들은 전부 고정값
			res.setContentType("application/octet-stream");
			res.setHeader("content-Disposition", "attachment; filename=\"" + dfName + "\"");
												//attachment; filename="그림1.png"
			
			//보내는 통로 설정
			os = res.getOutputStream();
			//파일 전송(byte단위로 전송)
			byte[] buffer = new byte[1024];
			int length;
			while((length = is.read(buffer)) != -1) {//읽어와서 (-1까지 전부 보내라)
				os.write(buffer, 0, length);
			}
		}catch (Exception e) {
			//e.printStackTrace();
		}finally {
			try {
				os.flush();
				os.close();
				is.close();
			} catch (Exception e2) {
				//e.printStackTrace();
			}
		}
	}
	//7. 댓글저장
	@Transactional
	public Map<String, List<ReplyDto>> replyInsert(ReplyDto reply){
		Map<String, List<ReplyDto>> rmap = null;
		try {
			//댓글 삽입
			bDao.replyInsert(reply);
			//댓글목록 다시검색
			List<ReplyDto> rList = bDao.getReList(reply.getR_bnum());
			rmap = new HashMap<String, List<ReplyDto>>();
			rmap.put("rList", rList);
		}catch (Exception e) {
			//e.printStackTrace();
			rmap = null;
		}
		//이 rmap은 boardContents.jsp의 script ajax의 success함수
		//에 result로 들어감.
		return rmap;
	}
	//8.게시물 업데이트 가져오기
	public ModelAndView updateFrm(int bnum, RedirectAttributes rttr) {
		mv = new ModelAndView();
		BoardDto board = bDao.getContents(bnum);//게시물정보가져온다
		MemberDto member = (MemberDto) session.getAttribute("mb");//세션정보에서 회원정보 가져온다
		//modelandview key value값을 담아서 보낼수 있다
		mv.addObject("board", board);
		
		//리스트에 파일정보를 담는다(oriname, sysname)
		List<BfileDto> bfList = bDao.getBfList(bnum);
		mv.addObject("bfList", bfList);
		mv.setViewName("updateFrm");
		return mv;
	}
	//9. 게시물 업데이트 수정 처리
	//MultipartHttpServletRequest
	//업로드된 파일에 액세스할 수 있도록 하는 서블릿 요청 내에서 멀티파트 콘텐츠를 처리하기 위한 추가 방법을 제공합니다
	@Transactional
	public String boardUpdate(MultipartHttpServletRequest multi, RedirectAttributes rttr) {
		String view = null;
		String bnum = multi.getParameter("bnum");
		String title = multi.getParameter("btitle");
		String contents = multi.getParameter("bcontents");
		String id = multi.getParameter("bid");
		String check = multi.getParameter("fileCheck");
		
		contents = contents.trim();
		log.info("boardUpdate() t : " + title + ", c: " + contents);
		
		BoardDto board = new BoardDto();
		board.setBnum(Integer.parseInt(bnum));
		board.setBtitle(title);
		board.setBcontents(contents);
		board.setBid(id);
		try {
			bDao.boardUpdate(board);
			//파일업로드 메소드 호출
			if(check.equals("1")) {
				fileUpload(multi, board.getBnum());
			}
			rttr.addFlashAttribute("msg", "수정성공");
		}catch (Exception e) {
			e.printStackTrace();
			rttr.addFlashAttribute("msg", "수정실패");
		}
		view = "redirect:contents?bnum=" + bnum;//글쓰기 상세페이지 이동
		
		return view;
	}
	//10. 개별 파일 삭제(게시물 수정시)
	public Map<String, List<BfileDto>> fileDelete(String sysname, int bnum){
		Map<String, List<BfileDto>> rMap = null;
		log.info("fileDelete() - sysname : " + sysname + ", bnum : " + bnum);
		//절대경로는 세션으로 구할수도 있다
		//getServletContext 경로확인용 
		String path = session.getServletContext().getRealPath("/");
		log.info(path);
		path += "resources/upload/" + sysname;
		try {
			bDao.fileDelete(sysname);
			//파일 객체 생성
			File file = new File(path);
			
			//경로에 파일이 존재하는지 확인
			if(file.exists()) {
				//삭제 성공시
				if(file.delete()) {
					List<BfileDto> fList = bDao.getBfList(bnum);
					rMap = new HashMap<String, List<BfileDto>>();
					rMap.put("fList", fList);
				}else {
					rMap = null;
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			rMap = null;
		}		
		return rMap;
	}
	//11. 게시글 삭제
	@Transactional
	public String boardDelete(int bnum, RedirectAttributes rttr) {
		log.info("boardDelete() - bnum : " + bnum);
		String view = null;
		
		try {
			//댓글삭제
			bDao.replyDelete(bnum);
			//파일삭제
			bDao.filesListDelete(bnum);
			//게시글 삭제
			bDao.boardDelete(bnum);
			
			view = "redirect:list";
			rttr.addFlashAttribute("msg", "삭제 성공");		
		}catch (Exception e) {
			e.printStackTrace();
			view = "redirect:contents?bnum=" + bnum;
			rttr.addFlashAttribute("msg", "삭제 실패");
		}		
		return view;
	}
}//class



































