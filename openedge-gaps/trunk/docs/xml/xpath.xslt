<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
 <xsl:output method="html" version="1.0" encoding="UTF-8" indent="no"/>
 
 <xsl:template match="/">
 	<html>
 	<body>
 	<xsl:apply-templates select="//allureplan/fondsen/fonds[@id='Levob Aandelen Fonds']/aankoopkosten/@waarde"/>
 	</body>
 	</html>
 </xsl:template>


</xsl:stylesheet>