<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xalan="http://xml.apache.org/xalan"
    exclude-result-prefixes="xalan">

    <xsl:param name="templateName" select="'NoTemplate'"/>

    <xsl:template match="/">
        <xsl:apply-templates select="*[$templateName]"/>
    </xsl:template>

    <xsl:template name="test">
        <xsl:apply-templates select="abc"/>
    </xsl:template>

    <!-- test templates -->
    <xsl:template match="abc">
        <xsl:element name="abc">
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="hello">
        <xsl:element name="hello">
            <xsl:element name="{@name}"/>
        </xsl:element>
    </xsl:template>

</xsl:stylesheet>
