<%--
	$Id: loginForm.jsp,v 1.4 2003/01/12 04:03:23 lhoriman Exp $
	$Source: /cvsroot-fuse/mav/maverick/examples/friendbook-jsp/content/loginForm.jsp,v $
	
	This file is @included.  Expects to have a page variable "dest" already set.
--%>

<%-- Doesn't work with @includes
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
--%>

<form action="loginSubmit.m" method="post">
	<input type="hidden" name="dest" value="<c:out value="${dest}"/>"/>
	<table border="0">
		<tr>
			<td align="right"> Login Name: </td>
			<td> <input type="text" name="name" value=""/> </td>
		</tr>
		<tr>
			<td align="right"> Password: </td>
			<td> <input type="password" name="password" value=""/> 
				 <input type="submit" value="Login"/> </td>
		</tr>
	</table>
</form>

<p>
	Would you like to <a href="signup.m">create an account</a>?
</p>
