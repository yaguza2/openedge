<?xml version="1.0" encoding="utf-8"?><xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:src="http://nwalsh.com/xmlns/litprog/fragment" exclude-result-prefixes="src" version="1.0">
  <xsl:attribute name="keep-with-next.within-column">always</xsl:attribute>
  <xsl:attribute name="space-before.minimum">0.8em</xsl:attribute>
  <xsl:attribute name="space-before.optimum">1.0em</xsl:attribute>
  <xsl:attribute name="space-before.maximum">1.2em</xsl:attribute>
</xsl:attribute-set>
<xsl:param name="segmentedlist.as.table" select="0"/>
<xsl:param name="shade.verbatim" select="0"/>

<xsl:attribute-set name="shade.verbatim.style">
  <xsl:attribute name="background-color">#E0E0E0</xsl:attribute>
</xsl:attribute-set>
<xsl:param name="show.comments">1</xsl:param>
<xsl:attribute-set name="sidebar.properties" use-attribute-sets="formal.object.properties">
  <xsl:attribute name="border-style">solid</xsl:attribute>
  <xsl:attribute name="border-width">1pt</xsl:attribute>
  <xsl:attribute name="border-color">black</xsl:attribute>
  <xsl:attribute name="background-color">#DDDDDD</xsl:attribute>
  <xsl:attribute name="padding-left">12pt</xsl:attribute>
  <xsl:attribute name="padding-right">12pt</xsl:attribute>
  <xsl:attribute name="padding-top">6pt</xsl:attribute>
  <xsl:attribute name="padding-bottom">6pt</xsl:attribute>
<!--
  <xsl:attribute name="margin-left">12pt</xsl:attribute>
  <xsl:attribute name="margin-right">12pt</xsl:attribute>
  <xsl:attribute name="margin-top">6pt</xsl:attribute>
  <xsl:attribute name="margin-bottom">6pt</xsl:attribute>
-->
</xsl:attribute-set>

<xsl:param name="table.cell.border.color" select="'black'"/>
<xsl:param name="table.cell.border.style" select="'solid'"/>
<xsl:param name="table.cell.border.thickness" select="'0.5pt'"/>
<xsl:attribute-set name="table.cell.padding">
  <xsl:attribute name="padding-left">2pt</xsl:attribute>
  <xsl:attribute name="padding-right">2pt</xsl:attribute>
  <xsl:attribute name="padding-top">2pt</xsl:attribute>
  <xsl:attribute name="padding-bottom">2pt</xsl:attribute>
</xsl:attribute-set>
<xsl:param name="table.footnote.number.format" select="'a'"/>
<xsl:param name="table.footnote.number.symbols" select="''"/>

<xsl:param name="table.frame.border.color" select="'black'"/>
<xsl:param name="table.frame.border.style" select="'solid'"/>
<xsl:param name="table.frame.border.thickness" select="'0.5pt'"/>
<xsl:attribute-set name="table.properties" use-attribute-sets="formal.object.properties"/>
<xsl:param name="tablecolumns.extension" select="'1'"/>
<xsl:param name="target.database.document" select="''"/>
<xsl:param name="tex.math.delims" select="'1'"/>
<xsl:param name="tex.math.in.alt" select="''"/>
<xsl:param name="textinsert.extension" select="'1'"/>
<xsl:param name="title.font.family" select="'sans-serif'"/>
<xsl:param name="title.margin.left" select="'-4pc'"/>
<xsl:param name="toc.indent.width" select="24"/>
<xsl:attribute-set name="toc.margin.properties">
  <xsl:attribute name="space-before.minimum">0.5em</xsl:attribute>
  <xsl:attribute name="space-before.optimum">1em</xsl:attribute>
  <xsl:attribute name="space-before.maximum">2em</xsl:attribute>
  <xsl:attribute name="space-after.minimum">0.5em</xsl:attribute>
  <xsl:attribute name="space-after.optimum">1em</xsl:attribute>
  <xsl:attribute name="space-after.maximum">2em</xsl:attribute>
</xsl:attribute-set>
<xsl:param name="toc.section.depth">2</xsl:param>
<xsl:param name="ulink.footnote.number.format" select="'1'"/>
<xsl:param name="ulink.footnotes" select="0"/>
<xsl:param name="ulink.hyphenate" select="''"/>
<xsl:param name="ulink.show" select="1"/>
<xsl:param name="use.extensions" select="'0'"/>
<xsl:param name="use.local.olink.style" select="0"/> 
<xsl:param name="use.role.as.xrefstyle" select="1"/>
<xsl:param name="use.role.for.mediaobject" select="1"/>
<xsl:param name="use.svg" select="1"/>
<xsl:param name="variablelist.as.blocks" select="0"/>
<xsl:attribute-set name="verbatim.properties">
  <xsl:attribute name="space-before.minimum">0.8em</xsl:attribute>
  <xsl:attribute name="space-before.optimum">1em</xsl:attribute>
  <xsl:attribute name="space-before.maximum">1.2em</xsl:attribute>
  <xsl:attribute name="space-after.minimum">0.8em</xsl:attribute>
  <xsl:attribute name="space-after.optimum">1em</xsl:attribute>
  <xsl:attribute name="space-after.maximum">1.2em</xsl:attribute>
</xsl:attribute-set>
<xsl:param name="xep.extensions" select="0"/>
<xsl:attribute-set name="xref.properties">
</xsl:attribute-set>
<xsl:param name="xref.with.number.and.title" select="1"/>

</xsl:stylesheet>