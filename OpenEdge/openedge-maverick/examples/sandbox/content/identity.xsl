<?xml version="1.0" encoding="utf-8" ?>
<!--
	$Id: identity.xsl,v 1.4 2001/06/11 16:44:47 skot Exp $
	$Source: /cvsroot-fuse/mav/maverick/examples/sandbox/content/identity.xsl,v $
-->


<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml"/>
    
    <xsl:template match="*|@*">
        <xsl:copy>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>