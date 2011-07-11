<?xml version="1.0"?>

<!--
	$Id: style-manual.xsl,v 1.4 2004/06/27 17:42:15 eelco12 Exp $
	$Source: /cvsroot-fuse/mav/maverick/tools/style-manual.xsl,v $
	
	Customizes the behavior of the standard DocBook XSLT transforms.
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="../../docbook-xsl/html/docbook.xsl"/>

	<xsl:output method="html" indent="yes" />

	<xsl:variable name="html.stylesheet">stylesheet.css</xsl:variable>
	
	<!--
		Need to change the order of copyright and releaseinfo, and add
		mediaobject to the title page.  How did they forget this?!?
	-->
	     
	<xsl:template name="book.titlepage.recto">
		<xsl:apply-templates select="bookinfo/mediaobject"/>
		
		<xsl:choose>
			<xsl:when test="bookinfo/title">
				<xsl:apply-templates mode="book.titlepage.recto.auto.mode" select="bookinfo/title"/>
			</xsl:when>
			<xsl:when test="title">
				<xsl:apply-templates mode="book.titlepage.recto.auto.mode" select="title"/>
			</xsl:when>
		</xsl:choose>

		<xsl:choose>
			<xsl:when test="bookinfo/subtitle">
				<xsl:apply-templates mode="book.titlepage.recto.auto.mode" select="bookinfo/subtitle"/>
			</xsl:when>
			<xsl:when test="subtitle">
				<xsl:apply-templates mode="book.titlepage.recto.auto.mode" select="subtitle"/>
			</xsl:when>
		</xsl:choose>

		<xsl:apply-templates mode="book.titlepage.recto.auto.mode" select="bookinfo/corpauthor"/>
		<xsl:apply-templates mode="book.titlepage.recto.auto.mode" select="bookinfo/authorgroup"/>
		<xsl:apply-templates mode="book.titlepage.recto.auto.mode" select="bookinfo/author"/>
		<xsl:apply-templates mode="book.titlepage.recto.auto.mode" select="bookinfo/othercredit"/>
		<xsl:apply-templates mode="book.titlepage.recto.auto.mode" select="bookinfo/copyright"/>
		<xsl:apply-templates mode="book.titlepage.recto.auto.mode" select="bookinfo/legalnotice"/>
		<xsl:apply-templates mode="book.titlepage.recto.auto.mode" select="bookinfo/pubdate"/>
		<xsl:apply-templates mode="book.titlepage.recto.auto.mode" select="bookinfo/releaseinfo"/>
		<xsl:apply-templates mode="book.titlepage.recto.auto.mode" select="bookinfo/revision"/>
		<xsl:apply-templates mode="book.titlepage.recto.auto.mode" select="bookinfo/revhistory"/>
		<xsl:apply-templates mode="book.titlepage.recto.auto.mode" select="bookinfo/abstract"/>
	</xsl:template>
</xsl:stylesheet>
