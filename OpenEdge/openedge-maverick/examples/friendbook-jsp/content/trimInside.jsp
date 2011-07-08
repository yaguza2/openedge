<%--
	$Id: trimInside.jsp,v 1.2 2002/05/31 03:12:27 lhoriman Exp $
	$Source: /cvsroot-fuse/mav/maverick/examples/friendbook-jsp/content/trimInside.jsp,v $
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
				<td class="pageTitle"> <h1> <c:out value="${title}"/> </h1> </td>
			</tr>
		</table>

		<div><br/></div>

		<table width="100%" cellspacing="0" cellpadding="5" border="0">
			<tr>
				<td width="1%" align="left" valign="top" class="navigationAll">
					<div class="navigationAll">
						<a class="nav" href="friends.m"> Friends </a>

						<br/>
						<br/>

						<a class="nav" href="edit.m"> Edit Info </a>

						<br/>
						<br/>

						<a class="nav" href="changePassword.m"> Change Password </a>

						<br/>
						<br/>

						<a class="nav" href="logout.m"> Logout </a>
					</div>
				</td>

				<td align="left" valign="top">
					<c:out value="${wrapped}" escapeXml="false"/> 
				</td>
			</tr>
		</table>

	</body>
</html>
