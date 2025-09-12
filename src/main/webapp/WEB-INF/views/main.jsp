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

  <a href="${contextPath}/user/list">회원 목록</a>
  <br>
  <a href="${contextPath}/notice/list">공지사항</a>
  
  <script type="text/javascript">
  	const msg = "${msg}";
  	if (msg !== "")
  	  alert(msg);
  </script>
  
</body>
</html>