<?xml version="1.0" encoding="UTF-8"?>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format"  font-size="16pt">
	<fo:layout-master-set>
		<fo:simple-page-master margin-right="0.5in" margin-left="0.5in" 
							margin-bottom="0.5in" margin-top="0.5in" 
							page-width="8.5in" page-height="11in" 
							master-name="page">

               <fo:region-before extent="1in"/>
               <fo:region-body region-name="body" margin-top="0.5in" margin-bottom="0.5in"/>
               <fo:region-after extent="0.5in"/>
		</fo:simple-page-master>
	</fo:layout-master-set>

	<fo:page-sequence master-reference="page">
		<fo:static-content flow-name="xsl-region-before">
			<fo:block text-align="center" font-size="18pt" font-family="serif" line-height="10pt">My Address Book</fo:block>
		</fo:static-content>
		<fo:static-content flow-name="xsl-region-after">
            <fo:block text-align="end" font-size="10pt" font-family="serif" line-height="14pt">
				page <fo:page-number/>
			</fo:block>
		</fo:static-content>
		
		<fo:flow flow-name="body">
	       <fo:table font-size="10pt">
	         <fo:table-column column-width="1in" />
	         <fo:table-column column-width="2.5in" />
	         <fo:table-column column-width="1in" />
	         <fo:table-column column-width="2.5in" />
			 <fo:table-header font-weight="bold" text-align="center">
	           <fo:table-row>
	             <fo:table-cell><fo:block>Name</fo:block></fo:table-cell>
	             <fo:table-cell><fo:block>Address</fo:block></fo:table-cell>
	             <fo:table-cell><fo:block>Phone Number</fo:block></fo:table-cell>
	             <fo:table-cell><fo:block>Email Address</fo:block></fo:table-cell>
	           </fo:table-row>
			 </fo:table-header>
			 <fo:table-body>
				<c:forEach var="friend" items="${model.friends}">
					<fo:table-row border-width="0.5pt">
			           <fo:table-cell padding="5pt" border-width="0.5pt" border-color="black" border-style="solid">
						 	<fo:block><c:out value="${friend.lastName}"/>, <c:out value="${friend.firstName}"/></fo:block>
						</fo:table-cell>
			            <fo:table-cell padding="5pt" border-width="0.5pt" border-color="black" border-style="solid">
							<c:if test="${friend.address != null}">
								<c:if test="${friend.address.addressLine1 != null}">
									<fo:block><c:out value="${friend.address.addressLine1}"/></fo:block>
								</c:if>
								<c:if test="${friend.address.addressLine2 != null}">
									<fo:block><c:out value="${friend.address.addressLine2}"/></fo:block>
								</c:if>
								<c:if test="${friend.address.city != null}">
									<fo:block><c:out value="${friend.address.city}"/></fo:block>
								</c:if>
								<c:if test="${friend.address.state != null}">
									<fo:block><c:out value="${friend.address.state}"/></fo:block>
								</c:if>
							</c:if>
						</fo:table-cell>
						<fo:table-cell padding="5pt" border-width="0.5pt" border-color="black" border-style="solid">
							<c:if test="${friend.phoneList != null}">
								<c:forEach var="phone" items="${friend.phoneList}">
									<fo:block><c:out value="${phone}"/></fo:block>
								</c:forEach>
							</c:if>
						</fo:table-cell>
						<fo:table-cell padding="5pt" border-width="0.5pt" border-color="black" border-style="solid">
							<c:if test="${friend.emailList != null}">
								<c:forEach var="email" items="${friend.emailList}">
									<fo:block><c:out value="${email}"/></fo:block>
								</c:forEach>
							</c:if>
						</fo:table-cell>
					</fo:table-row>
				</c:forEach>
	         </fo:table-body>
	       </fo:table>
		</fo:flow>
	</fo:page-sequence>
</fo:root>

