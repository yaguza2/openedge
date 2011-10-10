<%--
	$Id: trim.jsp,v 1.1 2002/01/03 11:48:39 lhoriman Exp $
	$Source: /cvsroot-fuse/mav/maverick/examples/shunting-jsp/content/trim.jsp,v $
--%>

<%@ page language="java" contentType="text/html" %>

<html>
	<head>
		<title> <%= request.getAttribute("title") %> </title>
		<link rel="stylesheet" href="stylesheet.css" type="text/css" />
	</head>

	<body>
		<table cellspacing="0" cellpadding="0" width="100%">
			<tr>
				<td class="pageTitle" colspan="2"> <h1> <%= request.getAttribute("title") %> </h1> </td>
			</tr>
			<tr align="center" style="text-align: center">
				<td class="navigationTop"> <a class="nav" href="welcome.m">Some Stuff</a> </td>
				<td class="navigationTop"> <a class="nav" href="other.m">Other Stuff</a> </td>
			</tr>
		</table>

		<%= request.getAttribute("wrapped") %>
	</body>
</html>
