<%--
	$Id: edit.jsp,v 1.4 2002/11/02 00:54:27 lhoriman Exp $
	$Source: /cvsroot-fuse/mav/maverick/examples/friendbook-jsp/content/edit.jsp,v $
--%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<c:set var="title" scope="request">Edit Your Info</c:set>


<p>
	Please make your changes:
</p>

<form action="editSubmit.m" method="post">
	<table border="0">
		<tr>
			<td class="editTitle">First Name:</td>
			<td><input name="firstName" value="<c:out value="${model.firstName}"/>"/></td>
			<td class="errorText">
				<c:out value="${model.errors['firstName']}"/>
			</td>
		</tr>
		<tr>
			<td class="editTitle">Last Name:</td>
			<td><input name="lastName" value="<c:out value="${model.lastName}"/>"/></td>
			<td class="errorText">
				<c:out value="${model.errors['lastName']}"/>
			</td>
		</tr>

		<tr>
			<td colspan="3">&nbsp;</td>
		</tr>
		<tr>
			<td class="editTitle">Address:</td>
			<td><input type="text" name="addrLine1" value="<c:out value="${model.addrLine1}"/>"/></td>
			<td class="errorText">
				<c:out value="${model.errors['addrLine1']}"/>
			</td>
		</tr>
		<tr>
			<td class="editTitle">(line 2)</td>
			<td><input type="text" name="addrLine2" value="<c:out value="${model.addrLine2}"/>"/></td>
			<td class="errorText">
				<c:out value="${model.errors['addrLine2']}"/>
			</td>
		</tr>
		<tr>
			<td class="editTitle">State:</td>
			<td><input type="text" name="addrState" value="<c:out value="${model.addrState}"/>"/></td>
			<td class="errorText">
				<c:out value="${model.errors['addrState']}"/>
			</td>
		</tr>
		<tr>
			<td class="editTitle">City:</td>
			<td><input type="text" name="addrCity" value="<c:out value="${model.addrCity}"/>"/></td>
			<td class="errorText">
				<c:out value="${model.errors['addrCity']}"/>
			</td>
		</tr>

		<tr>
			<td colspan="3">&nbsp;</td>
		</tr>
		<tr>
			<td colspan="3" style="font-weight: bold">Phone numbers:</td>
		</tr>

		<%-- list phone numbers --%>
		<c:set var="position">1</c:set>
		<c:forEach var="phone" items="${model.phoneList}">
			<tr>
				<td>del? <input type="checkbox" name="del_phone<c:out value="${position}"/>"/></td>
				<td>
					<input style="width: 250px;"
						type="text"
						name="phone<c:out value="${position}"/>"
						value="<c:out value="${phone}"/>"/>
				</td>
				<td class="errorText">
					<c:set var="key" value="phone${position}"/>
					<c:out value="${model.errors[$key]}"/>
				</td>
			</tr>
			<c:set var="position" value="${position + 1}"/>
		</c:forEach>

		<tr>
			<td>Add Number</td>
			<td>
				<input style="width: 250px;" type="text" name="phone<c:out value="${position}"/>" value=""/>
			</td>
		</tr>

		<tr>
			<td colspan="3" style="font-weight: bold">Email Addresses:</td>
		</tr>

		<%-- list emails --%>
		<c:set var="position">1</c:set>
		<c:forEach var="email" items="${model.emailList}">
			<tr>
				<td>del? <input type="checkbox" name="del_email<c:out value="${position}"/>"/></td>
				<td>
					<input style="width: 250px;"
						type="text"
						name="email<c:out value="${position}"/>"
						value="<c:out value="${email}"/>"/>
				</td>
				<td class="errorText">
					<c:set var="key" value="email${position}"/>
					<c:out value="${model.errors[$key]}"/>
				</td>
			</tr>
			<c:set var="position" value="${position + 1}"/>
		</c:forEach>
		<tr>
			<td>Add Email</td>
			<td><input style="width: 250px;" type="text" name="email<c:out value="${position}"/>" value=""/></td>
		</tr>
		<tr>
			<td></td>
			<td><input type="submit" value="Save"/></td>
		</tr>
	</table>
</form>
