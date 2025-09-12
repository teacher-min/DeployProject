<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.7.2/css/all.min.css" integrity="sha512-Evv84Mr4kqVGRNSgIGL/F/aIDqQb7xQ2vcrdIwxfjThSH8CSR7PBEakCr51Ck+w+/U6swU2Im1vVX0SVk9ABhg==" crossorigin="anonymous" referrerpolicy="no-referrer" />
<title>Insert title here</title>
</head>
<body>
  
  <a href="${contextPath}/notice/write">작성</a>
  
  <hr>
  
  <h1>공지사항</h1>
  <table border="1">
    <tbody>
      <c:forEach items="${notices}" var="notice">
        <tr>
          <td><a href="${contextPath}/notice/detail?nid=${notice.nid}">${notice.title}</a></td>
          <td>
            <c:if test="${notice.attachCount ne 0}">
              <i class="fa-solid fa-paperclip"></i>
              (${notice.attachCount})
            </c:if>
          </td>
        </tr>
      </c:forEach>
    </tbody>
  </table>
  
  <script type="text/javascript">
  	const msg = "${msg}";
  	if (msg !== "")
  	  alert(msg);
  </script>
  
</body>
</html>