<?xml version="1.0" encoding="utf-8" ?>
<!--
	$Id: simple.xsl,v 1.4 2001/06/19 08:57:31 lhoriman Exp $
	$Source: /cvsroot-fuse/mav/maverick/examples/sandbox/content/simple.xsl,v $
-->


<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html"/>
    <xsl:template match="/*">
        <html>
            <body>
                <p>(
                    <xsl:value-of select="local-name()"/>
                    <br/>
                    <xsl:value-of select="text()"/>
                )</p>
                
                <p>
                	Param $foo is <xsl:value-of select="$foo"/>
                </p>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>