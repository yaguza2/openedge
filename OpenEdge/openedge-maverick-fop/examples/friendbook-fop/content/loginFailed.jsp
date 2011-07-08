<%--
	$Id: loginFailed.jsp,v 1.3 2002/05/31 03:12:27 lhoriman Exp $
	$Source: /cvsroot/mav/maverick/examples/friendbook-jsp/content/loginFailed.jsp,v $
--%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<c:set var="title" scope="request">Login Failed</c:set>

<p>
	<span class="errorText">You have entered an invalid login name or bad password.</span>
</p>

<c:set var="dest" value="${model.destination}"/>

<%@ include file="loginForm.jsp" %>
