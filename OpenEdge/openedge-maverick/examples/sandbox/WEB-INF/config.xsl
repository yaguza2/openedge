<?xml version="1.0" encoding="utf-8" ?>
<!--
	$Id: config.xsl,v 1.1 2002/02/16 23:15:13 lhoriman Exp $
	$Source: /cvsroot-fuse/mav/maverick/examples/sandbox/WEB-INF/config.xsl,v $
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml"/>

	<xsl:template match="*|@*">
		<xsl:copy>
			<xsl:apply-templates/>
		</xsl:copy>
	</xsl:template>
    
    <xsl:template match="command[@name = 'welcome']">
		<xsl:copy>
			<xsl:apply-templates select="@*"/>
			<view type="document" path="hello.html"/>
			<xsl:apply-templates select="*"/>
		</xsl:copy>
    </xsl:template>
</xsl:stylesheet>