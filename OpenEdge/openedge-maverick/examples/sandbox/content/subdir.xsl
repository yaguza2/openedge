<?xml version="1.0" encoding="utf-8" ?>
<!--
	$Id: subdir.xsl,v 1.1 2001/10/16 22:17:05 lhoriman Exp $
	$Source: /cvsroot-fuse/mav/maverick/examples/sandbox/content/subdir.xsl,v $
-->


<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html"/>
    
    <xsl:template match="*|@*">
        <xsl:copy>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="link">
    	<a href="{.}"><xsl:value-of select="."/></a>
    </xsl:template>
</xsl:stylesheet>