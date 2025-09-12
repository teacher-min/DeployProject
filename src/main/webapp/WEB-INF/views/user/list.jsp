<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Insert title here</title>
</head>
<body>
  
  <h1>회원 정보 업로드</h1>
  <form method="post"
        action="${contextPath}/user/signup"
        enctype="multipart/form-data"
        id="signupForm">
    <label>이메일 : <input type="text" name="email"></label>
    <br>
    <input type="file" name="profile" id="profile">
    <br>
    <button type="submit">가입하기</button>
  </form>
  <c:if test="${not empty error}">
    <div style="font-size: 12px; color: red;">${error}</div>
  </c:if>
  
  <script type="text/javascript">
    const signupForm = document.getElementById("signupForm");
    const profile = document.getElementById("profile");
    signupForm.addEventListener("submit", function(e) {
      const file = profile.files[0];  //----- 첨부된 파일
      if (file) {
        const limit = 1024 * 1024 * 10;  //----- 10MB
        if (file.size > limit) {
          alert('첨부 파일 최대 크기는 10MB입니다.');
          file.value = "";  //----- 첨부된 파일 초기화
          e.preventDefault();  //-- 서브밋 방지
          return;
        }
      }
    })
  </script>
  
  <hr>
  
  <h1>회원 목록</h1>
  <table border="1">
    <tbody>
      <c:forEach items="${users}" var="user">
        <tr>
          <td><a href="${contextPath}/user/detail?uid=${user.uid}">${user.email}</a></td>
          <td>${user.originalFilename}</td>
        </tr>
      </c:forEach>
    </tbody>
  </table>
  
</body>
</html>