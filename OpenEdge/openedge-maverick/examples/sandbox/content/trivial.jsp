<%@ page language="java" %>
<%--
	$Id: trivial.jsp,v 1.3 2001/05/27 02:16:12 skot Exp $
	$Source: /cvsroot-fuse/mav/maverick/examples/sandbox/content/trivial.jsp,v $
--%>

<jsp:useBean id="model" scope="request" class="org.infohazard.sandbox.SimpleModel" />

<html>
	<head>
		<title>A JSP page!</title>
	</head>
	
	<body>
		<p>
			Two plus two is <%= 2 + 2 %>
		</p>
		<p>
			Prop is <jsp:getProperty name="model" property="prop" />
		</p>
	</body>
</html>