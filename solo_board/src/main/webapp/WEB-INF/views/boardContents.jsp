<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>    
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>희성 상세보기</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<link rel="stylesheet" href="resources/css/style.css">
<script type="text/javascript">
	$(function() {
		//메시지 출력 부분
		var msg = "${msg}";
		if (msg != "") {
			alert(msg)
		}
		//로그인한 회원 정보 및 로그아웃 출력
		var lname = "${mb.m_name}";
		$("#m_name").html(lname + "님");
		$(".suc").css("display", "block");
		$(".bef").css("display", "none");
		// 수정/삭제 버튼 처리(본인의 글이 아니면 수정/삭제 불가)
		$("#upbtn").hide();
		$("#delbtn").hide();
		
		var mid = "${mb.m_id}";
		var bid = "${board.bid}";
		
		if(mid == bid){
			$("#upbtn").show();
			$("#delbtn").show();
		}
	});
</script>
</head>
<body>
<div class="wrap">
		<header>
			<jsp:include page="header.jsp" />
		</header>
		<section>
			<div class="content">
				<div class="write-form">
					<div class="user-info">
						<div class="user-info-sub">
							<p class="grade">등급 [${mb.g_name}]</p>
							<p class="point">POINT [${mb.m_point}]</p>
						</div>
					</div>
					<h2 class="login-header">상세 보기</h2>
					<!-- 게시글 상세 내용 출력(table) -->
					<table>
						<tr height="30">
							<td width="100" bgcolor="pink" align="center">NUM</td>
							<td colspan="5">${board.bnum}</td>
						</tr>
						<tr height="30">
							<td bgcolor="pink" align="center">WRITER</td>
							<td width="150">${board.mname}</td>
							<td bgcolor="pink" align="center">DATE</td>
							<td width="200"><fmt:formatDate value="${board.bdate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
							<td bgcolor="pink" align="center">VIEWS</td>
							<td width="100">${board.bviews}</td>
						</tr>
						<tr height="30">
							<td bgcolor="pink" align="center">TITLE</td>
							<td colspan="5">${board.btitle}</td>
						</tr>
						<tr height="200">
							<td bgcolor="pink" align="center">CONTENTS</td>
							<td colspan="5" bgcolor="white">${board.bcontents}</td>
						</tr>
						<tr>
							<th>첨부파일</th>
							<td colspan="5">
								<!-- 첨부파일이 없을 경우 -->
								<c:if test="${empty fList}">
									첨부된 파일이 없습니다
								</c:if>
								<!-- 첨부파일이 있을 경우 -->
								<c:if test="${!empty fList }">
									<!-- 첨부파일이 여러개인 경우를 기본으로 처리 -->
									<c:forEach var="fitem" items="${fList}">
										<a href="./download?bf_sysname=${fitem.bf_sysname}&bf_oriname=${fitem.bf_oriname}"><!-- bfileDto 변수명 -->
											<span class="file-title">
												&nbsp;${fitem.bf_oriname}&nbsp;
											</span>
										</a>
									</c:forEach>
								</c:if>
							</td>
						</tr>
							<!-- 파일 존재 시  -->
							<c:if test="${!empty fList}">							
						<tr>
							<!-- 이미지 파일 미리보기 -->
							<th>PRIVIEW</th>
							<td colspan="5">
								<c:forEach var="f" items="${fList}"><!-- 이미지 파일 미리보기 -->
									<c:if test="${fn:contains(f.bf_sysname, '.jpg')}">
										<img src="resources/upload/${f.bf_sysname}" width="100">
									</c:if>
									<c:if test="${fn:contains(f.bf_sysname, '.png')}">
										<img src="resources/upload/${f.bf_sysname}" width="100">
									</c:if>
									<c:if test="${fn:contains(f.bf_sysname, '.gif')}">
										<img src="resources/upload/${f.bf_sysname}" width="100">
									</c:if>
								</c:forEach>
							</td>
						</tr>
							</c:if>
						<tr>
							<td colspan="6" align="center">
								<button class="btn-write" id="upbtn" onclick="location.href='./updateFrm?bnum=${board.bnum}'">U</button>
								<button class="btn-write" id="delbtn" onclick="delCheck(${board.bnum})">D</button>
								<button class="btn-sub" onclick="location.href='./list?pageNum=${pageNum}'">B</button>
							</td>
						</tr>
					</table>
					<!-- 댓글 작성 양식 -->
					<form id="rFrm">
						<textarea rows="3" class="write-input ta" name="r_contents" id="comment" placeholder="댓글을 적어주세요~"></textarea><!-- replydto에서 r_content -->
						<input type="button" value="댓글 전송" class="btn-write" onclick="replyInsert(${board.bnum})" style="width: 100%; margin-bottom: 30px;">
					</form>
					<!-- 댓글 목록 보기 -->
					<table style="width: 100%;"><!-- 제목 테이블 -->
						<tr bgcolor="pink" align="center" height="30">
							<td width="20%">WRITER</td>
							<td width="50%">CONTENTS</td>
							<td width="30%">DATE</td>
						</tr>
					</table>
					<table id="rtable" style="width: 100%"><!-- 목록 테이블 -->
						<c:forEach var="r" items="${rList}"><!-- 보드서비스  -->
							<tr>
								<td width="20%">${r.r_id}</td><!-- replydto -->
								<td width="50%">${r.r_contents}</td>
								<td width="30%">
									<fmt:formatDate value="${r.r_date}" pattern="yyyy-MM-dd HH:mm:ss"/>
								</td>
							</tr>
						</c:forEach>
					</table>
				</div>
			</div>
		</section>
		<footer>
			<jsp:include page="footer.jsp" />
		</footer>
	</div>
</body>
<!-- 댓글 전송 및 댓글 목록 새로고침용 스크립트 -->
<script src="resources/js/jquery.serializeObject.js"></script>
<script type="text/javascript">
	function replyInsert(bnum) {
		console.log(bnum);
		
		var replyFrm = $("#rFrm").serializeObject();//댓글 내용
		replyFrm.r_bnum = bnum;//게시글 번호
		replyFrm.r_id = "${mb.m_id}";//댓글작성자 아이디
		console.log(replyFrm);
		
		//controller에 ajax로 전송
		$.ajax({
			url : "replyIns",
			type : "post",
			data : replyFrm,
			dataType : "json",
			success : function (result) {//result에 rmap이 들어감
				//sevice와controller 연계
				var dlist = "";
				var rlist = result.rList;//rmap에서 rList를 꺼냄
				console.log(rlist);//내용확인 필!
				
				//목록이라서 반복처리!
				for(var i = 0; i < rlist.length; i++){
					dlist += "<tr>" + "<td width='20%'>" + rlist[i].r_id + "</td>" 
									+ "<td width='50%'>" + rlist[i].r_contents + "</td>"
									+ "<td width='30%'>" + rlist[i].r_date + "</td>"
						   + "</tr>";
				}
				$("#rtable").html(dlist);
			},
			error : function (error) {
				console.log(error);
				alert("댓글 입력 실패");
			}
		});
	}
	function delCheck(bnum) {
		var conf = confirm("삭제하시겠습니까?");
		
		if(conf == true){
			location.href='./delete?bnum=' + bnum;
		}
	}
</script>
</html>



































