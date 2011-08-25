<?xml version="1.0"?>

<!--
	$Id: style-website.xsl,v 1.9 2004/06/27 17:42:15 eelco12 Exp $
	$Source: /cvsroot-fuse/mav/maverick/tools/style-website.xsl,v $
	
	Customizes the behavior of the standard DocBook XSLT transforms.
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="../../docbook-xsl/html/docbook.xsl"/>

	<xsl:output method="html" indent="yes" />

	<xsl:template match="/book">
		<html>
			<head>
				<title> <xsl:value-of select="title" /> </title>
				<link rel="stylesheet" href="stylesheet.css" type="text/css" />
			</head>

			<body>
				<table border="0" cellspacing="0" cellpadding="4">
					<tr>
						<td colspan="3" align="center">
							<!-- Show the logo -->
							<xsl:apply-templates select="bookinfo"/>

							<hr/>
						</td>
					</tr>
					<tr>
						<td valign="top">
							<!-- Navigation bar -->
							<xsl:call-template name="navigation"/>
						</td>
						<td style="background-color: #cc9900">&#160;</td>
						<td valign="top">
							<!-- The body of the page -->
							<xsl:apply-templates select="preface/*"/>
						</td>
					</tr>
					<tr>
						<td colspan="3">
							<xsl:call-template name="sourceforgeLogo"/>				
						</td>
					</tr>
				</table>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="bookinfo">
		<!-- Print a nice header -->
		<img src="{mediaobject/imageobject/imagedata/@fileref}"/>
	</xsl:template>
	
	<xsl:template name="sourceforgeLogo">
		<div align="center">
			<a href="http://sourceforge.net">
				<img src="http://sourceforge.net/sflogo.php?group_id=26951" width="88" height="31" border="0" alt="SourceForge Logo"/>
			</a>
		</div>
	</xsl:template>
	
	<xsl:template name="navigation">
		<p><strong>Navigation</strong></p>
		<ul>
			<li><a href="http://mav.sourceforge.net/">Website</a></li>
			<li><a href="http://sourceforge.net/projects/mav/">Sourceforge&#160;Site</a></li>
			<li><a href="http://sourceforge.net/project/showfiles.php?group_id=26951">Downloads</a></li>
			<li><a href="http://sourceforge.net/tracker/?group_id=26951&amp;atid=388763">Bugs</a></li>
		</ul>
		
		<p><strong>Documentation</strong></p>
		<ul>
			<li><a href="maverick-manual.html">Manual</a></li>
			<li><a href="api/index.html">Javadocs</a></li>
			<li><a href="http://lists.sourceforge.net/lists/listinfo/mav-user">Mailing&#160;List</a><br/>(low traffic)</li>
			<li><a href="http://www.mail-archive.com/mav-user%40lists.sourceforge.net/">List&#160;Archives</a></li>
			<li><a href="maverick-manual.html#faq">FAQ</a></li>
		</ul>

		<p><strong>Additional Examples</strong></p>
		<ul>
			<li>
				<a href="http://mav.sourceforge.net/pig/">Punk&#160;Image&#160;Gallery</a><br/>
				(currently obsolete)
			</li>
		</ul>

		<p><strong>Related Links</strong></p>
		<ul>
			<li><a href="http://mavnet.sourceforge.net/">.NET Maverick</a></li>
			<li><a href="http://amb.sourceforge.net/">PHP Maverick</a></li>
			<li><a href="http://baritus.sourceforge.net/">Baritus</a></li>
			<li><a href="http://domify.sourceforge.net/">Domify</a></li>
			<li><a href="http://jakarta.apache.org/velocity/">Velocity</a></li>
			<li><a href="http://jakarta.apache.org/taglibs/doc/standard-doc/intro.html">JSTL</a></li>
		</ul>
	</xsl:template>
	
	<!--
	<xsl:template match="para">
		<p> <xsl:value-of select="." /> </p>
	</xsl:template>


	<xsl:template match="itemizedlist">
	</xsl:template>
	
	<xsl:template match="title">
		<xsl:if test="parent::book">
			<table cellspacing="0" cellpadding="0" width="100%">
				<tr>
					<td class="pageTitle"> <h1> <xsl:value-of select="." /> </h1> </td>
				</tr>
			</table>
			
			<p></p>
		</xsl:if>
		
		<xsl:if test="parent::sect1">
			<table cellspacing="0" cellpadding="0" width="100%">
				<tr>
					<td class="sectionTitle"> <xsl:value-of select="." /> </td>
				</tr>
			</table>
		</xsl:if>
	</xsl:template>

	-->

</xsl:stylesheet>
