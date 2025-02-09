<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>

<script	src="${pageContext.request.contextPath}/resources/js/jquery-3.6.0.min.js"></script>
<script	src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/common.js"></script>

<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/noticecss/css.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/fontawesome.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/all.min.css">

</head>
<body>
	<div class="wrap">
		<jsp:include page="../layout/jsp/header.jsp"></jsp:include>
		<div class="container">
			<div class="contents">
				<section class="notice_list">
					<div class="news_wrap">
						<div class="board_title">
							<strong>뉴스</strong>
						</div>
						<div class="news_view_wrap">
							<div class="title">${newsView.news_title}</div>
							<div class="info">
								<dl>
									<dt>글쓴이</dt>
									<dd>${newsView.news_writer}</dd>
								</dl>
								<dl>
									<dt>작성일</dt>
									<dd>${newsView.news_reg_date}</dd>
								</dl>
								<dl>
									<dt>조회수</dt>
									<dd>${newsView.news_view}</dd>
								</dl>
							</div>
							<div class="cont">${newsView.news_contents}</div>
						</div>
						<div class="news_bt_wrap">
							<a href="${pageContext.request.contextPath}/newsBoard.news?cpage=1" class="btn_s btn_light">목록</a> 
							<%--<c:if test="${login.user_id == admin_id} || ${login.user_id == newsView.news_writer}"> --%>
							<a href="${pageContext.request.contextPath}/newsModify.news?news_seq=${newsView.news_seq}" class="btn_s btn_primary" id="newsmodify">수정</a> 
							<a href="${pageContext.request.contextPath}/newsDelete.news?news_seq=${newsView.news_seq}" class="btn_s btn_default" id="newsdelete">삭제</a>
							<%--</c:if> --%>
						</div>
					</div>
				</section>
			</div>
		</div>
	</div>
	<jsp:include page="../layout/jsp/footer.jsp"></jsp:include>
</body>
</html>