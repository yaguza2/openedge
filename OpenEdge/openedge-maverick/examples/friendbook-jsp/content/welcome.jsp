<%--
	$Id: welcome.jsp,v 1.4 2002/05/31 03:12:27 lhoriman Exp $
	$Source: /cvsroot-fuse/mav/maverick/examples/friendbook-jsp/content/welcome.jsp,v $
--%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<c:set var="title" scope="request">Welcome</c:set>

<p>
	Welcome to the Friendbook example.  This is a simple contact-list
	application which demonstrates how to create a membership-based
	website with Maverick.
</p>

<c:set var="dest">friends.m</c:set>
<%@ include file="loginForm.jsp" %>
