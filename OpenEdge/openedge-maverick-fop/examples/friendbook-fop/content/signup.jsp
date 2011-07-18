<%--
	$Id: signup.jsp,v 1.4 2002/05/31 03:12:27 lhoriman Exp $
	$Source: /cvsroot/mav/maverick/examples/friendbook-jsp/content/signup.jsp,v $
--%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<c:set var="title" scope="request">Sign Up</c:set>

<p>
	To create an account, just fill out this form:
</p>

<form action="signupSubmit.m" method="post">
	<table border="0">
		<tr>
			<td align="right"> Login Name: </td>
			<td> <input type="text" name="loginName" value="<c:out value="${model.loginName}"/>" /> </td>
			<td class="errorText"><c:out value="${model.errors['loginName']}"/></td>
		</tr>
		<tr>
			<td align="right"> Password: </td>
			<td> <input type="password" name="password" value="<c:out value="${model.password}"/>"/> </td>
			<td class="errorText"><c:out value="${model.errors['password']}"/></td>
		</tr>
		<tr>
			<td align="right"> Password Again: </td>
			<td> <input type="password" name="passwordAgain" value="<c:out value="${model.passwordAgain}"/>"/> </td>
			<td class="errorText"><c:out value="${model.errors['passwordAgain']}"/></td>
		</tr>
		<tr>
			<td></td>
			<td> <input type="submit" value="Signup"/> </td>
		</tr>
	</table>
</form>
				