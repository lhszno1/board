<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
<title>희성게시판 홈</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script src="https://cdn.jsdelivr.net/bxslider/4.2.12/jquery.bxslider.min.js"></script>
<link rel="stylesheet" href="https://cdn.jsdelivr.net/bxslider/4.2.12/jquery.bxslider.css">
<link rel="stylesheet" href="resources/css/style.css">
<script type="text/javascript">
$(function () {
	//메세지 출력부분
	var msg = "${msg}";
	if(msg != ""){
		alert(msg);
	}
	//bxslider 설정용 스크립트
	$(".slider").bxSlider({
		auto : true,
		sliderWidth : 600,
	});
	//화면 크기 변경(반응형)에 따라 bxslider 재설정
	var mql = windows.matchMedia("screen and (max-width : 768px)");
	mql.addListener(function (e) {
		if(!e.matches){
			slider.reloadSlider();
		}
	})
});
</script>
</head>
<body>
	<div class="wrap">
		<header>
			<jsp:include page="header.jsp" />
		</header>
		<section>
			<div class="content-home">
				<div class="slider">
					<div><img src="resources/images/cat1.jpg"></div>
					<div><img src="resources/images/cat2.jpg"></div>
					<div><img src="resources/images/cat3.jpg"></div>
					<div><img src="resources/images/cat4.jpg"></div>
				</div>
			</div>
		</section>
		<footer>
			<jsp:include page="footer.jsp" />
		</footer>
	</div>
</body>
</html>
