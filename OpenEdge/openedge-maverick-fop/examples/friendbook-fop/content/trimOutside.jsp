<%--
	$Id: trimOutside.jsp,v 1.2 2002/05/31 03:12:27 lhoriman Exp $
	$Source: /cvsroot/mav/maverick/examples/friendbook-jsp/content/trimOutside.jsp,v $
--%>

<%@ page language="java" contentType="text/html" %>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<html>
	<head>
		<title> <c:out value="${title}"/> </title>
		<link rel="stylesheet" href="stylesheet.css" type="text/css" />
	</head>

	<body>
		<table cellspacing="0" cellpadding="0" width="100%">
			<tr>
				<td class="pageTitle" colspan="2"> <h1> <c:out value="${title}"/> </h1> </td>
			</tr>
			<tr align="center" style="text-align: center">
				<td class="navigationTop"> <a class="nav" href="welcome.m">Login</a> </td>
				<td class="navigationTop"> <a class="nav" href="signup.m">Sign Up</a> </td>
			</tr>
		</table>

		<c:out value="${wrapped}" escapeXml="false"/>
	</body>
</html>
