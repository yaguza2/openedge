<%--
	$Id: changePassword.jsp,v 1.3 2002/05/31 03:12:27 lhoriman Exp $
	$Source: /cvsroot/mav/maverick/examples/friendbook-jsp/content/changePassword.jsp,v $
--%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<c:set var="title" scope="request">Change Password</c:set>


<p>
	You can change your password by entering it here:
</p>

<form method="post" action="changePasswordSubmit.m">
	<table border="0">
		<tr>
			<td align="right"> Old Password: </td>
			<td>
				<input value="<c:out value="${model.oldPassword}"/>" name="oldPassword" id="oldPassword" type="password">
			</td>
			<td class="errorText">
				<c:out value="${model.errors['oldPassword']}"/>
			</td>
		</tr>
		<tr>
			<td align="right"> New Password: </td>
			<td>
				<input value="<c:out value="${model.newPassword}"/>" name="newPassword" type="password">
			</td>
			<td class="errorText">
				<c:out value="${model.errors['newPassword']}"/>
			</td>
		</tr>
		<tr>
			<td align="right"> New Password Again: </td>
			<td>
				<input value="<c:out value="${model.newPasswordAgain}"/>" name="newPasswordAgain" type="password">
			</td>
			<td class="errorText">
				<c:out value="${model.errors['newPasswordAgain']}"/>
			</td>
		</tr>
		<tr>
			<td></td><td><input value="Change" type="submit"></td>
		</tr>
	</table>
</form>
