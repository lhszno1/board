<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>회원 가입</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<link rel="stylesheet" href="resources/css/style.css">
<script type="text/javascript">
$(function () {
	//메세지 출력부분
	var msg = "${msg}";
	if(msg != ""){
		alert(msg);
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
			<!-- name 은 자바스크립트에서 사용 -->
				<form name="joinFrm" class="login-form" action="./memInsert" method="post" onsubmit="return check()">
				<h2 class="login-header">회원 가입</h2>
				<input type="text" class="login-input" id="mid" title="아이디" name="m_id" autofocus placeholder="아이디">
					<input type="button" class="idcheck-btn" value="중복확인" onclick="idcheck()"> 
					<input type="password" class="login-input" title="비밀번호" name="m_pwd" placeholder="비밀번호">
					<input type="text" class="login-input" title="이름" name="m_name" placeholder="이름">
					<input type="text" class="login-input" title="생일" name="m_birth" placeholder="생일">
					<input type="text" class="login-input" title="주소" name="m_addr" placeholder="주소">
					<input type="text" class="login-input" title="연락처" name="m_phone" placeholder="연락처">
					<input type="submit" class="login-btn" value="가입">
				</form>
			</div>
		</section>
		<footer>
			<jsp:include page="footer.jsp" />
		</footer>
	</div>
</body>
<!-- id 중복처리 -->
<script type="text/javascript">

	var ckOk = false;
	
function idcheck() {
	var inId = $("#mid").val();
	if(inId == ""){
		alert("아이디를 입력하세요");
		$("#mid").focus();
		return;
	}
	var ckObj = {"mid" : inId};//mid는 controller에 매개변수값동일
	
	$.ajax({
		url : "idCheck",//controller에 어노테이션 value값으로
		type : "get",
		data : ckObj,
		success : function (result) {
			if(result == "ok"){
				alert("아이디 사용가능");
				ckOk = true;
			}else{
				alert("사용할 수 없는 아이디");
				$("#mid").val("");
				$("#mid").focus();
				okOk = false;
			}
		},
		error : function (error) {
			console.log(error);
			ckOk = false;
		}
	});
}	
//입력 양식에 빈칸이 있는지 확인하는 함수
function check() { //form onsubmit return 값이 true false냐에 따라 true면 실행
	if(ckOk == false){
		alert("아이디 중복 체크를 해주세요");
		return false;
	}
	var frm = document.joinFrm;
	console.log(frm);
	var length = frm.length - 1;//form type이 포함된 tag중에서 submit은 뺀다
	for(var i =0; i < length; i++){
		if(frm[i].value == "" || frm[i].value == null){
			alert(frm[i].title + "입력!");
			frm[i].focus();
			return false;
		}
	}
	return true;
}
</script>
</html>



























