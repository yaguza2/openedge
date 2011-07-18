<%--
	$Id: loginFailed.jsp,v 1.4 2003/01/12 04:03:22 lhoriman Exp $
	$Source: /cvsroot-fuse/mav/maverick/examples/friendbook-jsp/content/loginFailed.jsp,v $
--%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<c:set var="title" scope="request">Login Failed</c:set>

<p>
	<span class="errorText">You have entered an invalid login name or bad password.</span>
</p>

<c:set var="dest" value="${model.dest}"/>

<%@ include file="loginForm.jsp" %>
