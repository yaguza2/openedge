<%--
	$Id: loginRequired.jsp,v 1.4 2003/01/12 04:03:23 lhoriman Exp $
	$Source: /cvsroot-fuse/mav/maverick/examples/friendbook-jsp/content/loginRequired.jsp,v $
--%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<c:set var="title" scope="request">Login Required</c:set>

		
<p>
	You must log in first.
</p>

<c:set var="dest" value="${model.dest}"/>

<%@ include file="loginForm.jsp" %>
