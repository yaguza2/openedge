Optional Fop Transform module for the Maverick MVC Framework

Version 1.1.1

This adds an extra Transform type which makes it very easy
to tranform a view through Apache Fop (http://xml.apache.org/fop)
to produce a variety of outputs.

To use opt-fop, simply add a transform node to a view as below:

	<view name="success" path="/pdf/friends.jsp">
		<transform type="fop"/>
	</view>


The default output type is pdf. If you need an alternate output,
use the optional "output" parameter:

	<view name="success" path="/pdf/friends.jsp">
		<transform type="fop" output="ps"/>
	</view>

opt-fop supports all Fop output formats with the exception of 
AWT and print which are set up to work with the local environment
and don't make sense in a web environment.See below for a full list
of supported outputs:

pdf: 		pdf
ps:			postscript
postscript:	postscript
text:		text
txt:		text
xml:		xml
svg:		svg (scalable vector graphics)
mif:		Maker Interchange Format which is used by Adobe Framemaker
pcl:		PCL (for Hewlett-Packard printers)

More info fop outputs: http://xml.apache.org/fop/output.html

This release of opt-fop is designed to work with Maverick 2.1, and as such
can be used in a chain of transforms:

	<view name="success" path="friends.jsp">
		<transform path="trimInside.jsp"/>
		<transform type="xslt" path="html_to_fo.xsl"/>
		<transform type="fop"/>
	</view>

There are two additional optional attributes that can be used on the transform node:

config: This tells fop to load the specified userconfig file. Value should be a
	path relative to the webapp root.

		<transform type="fop" config="/WEB-INF/userconfig.xml"/>



filename: If set, the Content-Disposition header will be overridden, setting the
	  disposition type to attachment using the filename you specified.

		<transform type="fop" filename="Friends.pdf"/>


Note: opt-fop reads the character encoding to be used out of the xml tag of the
xml string passed in as input. If the input to fop was produced by an xsl file,
this will probably be UTF-8. If it is produced by a jsp, this will probably be
ISO-8859-1. Be sure to set this encoding properly in the relavant file. See
friendsPdf.jsp and html_to_fo.xsl in the friendbook-fop example for an example
of this.

Thanks,
Jim Moore