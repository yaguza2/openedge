<%@ page language="java" %>
<%--
	$Id: xml.jsp,v 1.3 2001/05/27 02:16:12 skot Exp $
	$Source: /cvsroot-fuse/mav/maverick/examples/sandbox/content/xml.jsp,v $
--%>

<jsp:useBean id="model" scope="request" class="org.infohazard.sandbox.SimpleModel" />


<somexml>
	<sometext>
		Two plus two is <%= 2 + 2 %>
	</sometext>
	<prop>
		<jsp:getProperty name="model" property="prop" />
	</prop>
</somexml>