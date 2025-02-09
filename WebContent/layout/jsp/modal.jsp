<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="modal fade" id="loginModal" tabindex="-1"
	aria-labelledby="exampleModalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<img
					src="${pageContext.request.contextPath}/resources/images/login_image4.png"
					id="popup-img">
			</div>
			<div class="modal-header" id="modal-logo">
				<img
					src="${pageContext.request.contextPath}/resources/images/login_logo6.png"
					id="popup-logo">
			</div>

			<form action="${pageContext.request.contextPath}/loginProc.mem">
				<div class="modal-body">
					<div class="form-group">
						<label for="user-id" class="col-form-label"></label> <input
							type="text" class="form-control" id="user_id" name="user_id"
							placeholder="Your ID">
					</div>
					<div class="form-group">
						<label for="user-password" class="col-form-label"></label> <input
							type="password" class="form-control" id="user_pw"
							name="user_password" placeholder="Password">
					</div>

				</div>
				<div class="modal-footer">
					<button class="btn_m btn_default" data-dismiss="modal"
						style="width: 125%; margin-bottom: 16px;">로그인</button>

					<a href="${pageContext.request.contextPath}/findAccount.mem"><button
							type="button" class="find" data-dismiss="modal" id="idPwHov">아이디/비밀번호
							찾기</button></a>

				</div>
			</form>
		</div>
	</div>
</div>