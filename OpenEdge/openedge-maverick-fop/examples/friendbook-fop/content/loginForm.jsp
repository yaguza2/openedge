<%--
	$Id: loginForm.jsp,v 1.3 2002/05/31 03:12:27 lhoriman Exp $
	$Source: /cvsroot/mav/maverick/examples/friendbook-jsp/content/loginForm.jsp,v $
	
	This file is @included.  Expects to have a page variable "dest" already set.
--%>

<%-- Doesn't work with @includes
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
--%>

<form action="<c:out value="${dest}"/>" method="post">
	<table border="0">
		<tr>
			<td align="right"> Login Name: </td>
			<td> <input type="text" name="authName" value=""/> </td>
		</tr>
		<tr>
			<td align="right"> Password: </td>
			<td> <input type="password" name="authPassword" value=""/> 
				 <input type="submit" value="Login"/> </td>
		</tr>
	</table>
</form>

<p>
	Would you like to <a href="signup.m">create an account</a>?
</p>
