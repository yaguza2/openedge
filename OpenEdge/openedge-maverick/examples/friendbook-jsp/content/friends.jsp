<%--
	$Id: friends.jsp,v 1.3 2002/05/31 03:12:27 lhoriman Exp $
	$Source: /cvsroot-fuse/mav/maverick/examples/friendbook-jsp/content/friends.jsp,v $
--%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<c:set var="title" scope="request">Friends</c:set>


<p>
	Here is a list of all of your friends:
</p>

<table cellspacing="1" cellpadding="5" border="0" align="center">
	<tr>
		<th class="friendTitle">Name</th>
		<th class="friendTitle">Address</th>
		<th class="friendTitle">Phone Numbers</th>
		<th class="friendTitle">Email Addresses</th>
	</tr>
	<c:forEach var="friend" items="${model.friends}">
		<c:choose>
			<c:when test="${friend.login == model.myLogin}">
				<c:set var="styleAttrs">background-color: yellow</c:set>
			</c:when>
			<c:otherwise>
				<c:set var="styleAttrs"></c:set>
			</c:otherwise>
		</c:choose>

		<tr>
			<td class="friendValue" style="<c:out value="${styleAttrs}"/>" align="left" valign="top">
				<c:out value="${friend.lastName}"/>,&nbsp;<c:out value="${friend.firstName}"/>
				<c:if test="${friend.login == model.myLogin}">
					<br/>(<a href="edit.m">Edit</a>)
				</c:if>
			</td>
			<td class="friendValue" style="<c:out value="${styleAttrs}"/>" align="left" valign="top">
				<%-- check for address information before processing --%>
				
				<c:choose>
					<c:when test="${friend.address != null}">
						<%-- we use the test to exclude extra line breaks --%>
						
						<c:if test="${friend.address.addressLine1 != null}">
							<c:out value="${friend.address.addressLine1}"/><br/>
						</c:if>
						<c:if test="${friend.address.addressLine2 != null}">
							<c:out value="${friend.address.addressLine2}"/><br/>
						</c:if>
						<c:if test="${friend.address.city != null}">
							<c:out value="${friend.address.city}"/><br/>
						</c:if>
						<c:if test="${friend.address.state != null}">
							<c:out value="${friend.address.state}"/><br/>
						</c:if>
					</c:when>
					<c:otherwise>
						&nbsp;
					</c:otherwise>
				</c:choose>
			</td>
			<td class="friendValue" style="<c:out value="${styleAttrs}"/>" align="left" valign="top">
				<%-- check for phone information before processing --%>

				<c:choose>
					<c:when test="${friend.phoneList != null}">
						<c:forEach var="phone" items="${friend.phoneList}">
							<c:out value="${phone}"/><br/>
						</c:forEach>
					</c:when>
					<c:otherwise>
						&nbsp;
					</c:otherwise>
				</c:choose>
			</td>
			<td class="friendValue" style="<c:out value="${styleAttrs}"/>" align="left" valign="top">
				<c:choose>
					<c:when test="${friend.emailList != null}">
						<c:forEach var="email" items="${friend.emailList}">
							<c:out value="${email}"/><br/>
						</c:forEach>
					</c:when>
					<c:otherwise>
						&nbsp;
					</c:otherwise>
				</c:choose>
			</td>
		</tr>
	</c:forEach>
</table>
