<%@ page language="java" %>
<%--
	$Id: wrapper.jsp,v 1.1 2002/06/06 12:23:53 lhoriman Exp $
	$Source: /cvsroot-fuse/mav/maverick/examples/sandbox/content/wrapper.jsp,v $
--%>

<html>
	<head>
		<title>Wrapper</title>
	</head>
	
	<body>
		<p>
			Wrapped is:  <%= request.getAttribute("wrapped") %>
		</p>
	</body>
</html>