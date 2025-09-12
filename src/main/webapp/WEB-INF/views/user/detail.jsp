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
  
  <h1>회원 정보 확인</h1>
  
  <div>이메일 : ${user.email}</div>
  <div>프로필 이미지</div>
  <div>
    <img alt="프로필" src="${contextPath}${user.filePath}/${user.filesystemName}">
  </div>
  
</body>
</html>