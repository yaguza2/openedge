<%--
	$Id: loginRequired.jsp,v 1.3 2002/05/31 03:12:27 lhoriman Exp $
	$Source: /cvsroot/mav/maverick/examples/friendbook-jsp/content/loginRequired.jsp,v $
--%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<c:set var="title" scope="request">Login Required</c:set>

		
<p>
	You must log in first.
</p>

<c:set var="dest" value="${model.destination}"/>

<%@ include file="loginForm.jsp" %>
