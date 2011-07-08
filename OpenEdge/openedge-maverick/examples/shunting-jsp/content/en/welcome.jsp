<%--
	$Id: welcome.jsp,v 1.2 2002/01/10 10:49:18 lhoriman Exp $
	$Source: /cvsroot-fuse/mav/maverick/examples/shunting-jsp/content/en/welcome.jsp,v $
--%>

<% request.setAttribute("title", "Welcome"); %>

<p>
	This is a very simple web application which demostrates "shunting".
	Shunting allows you to attach modes to different views which have
	the same name, and Maverick will automatically pick the right view
	to render.  This example uses the LanguageShuntFactory, which
	uses the browser's Accept-Language header to determine mode.
</p>

<p>
	Try these pages again after configuring your browser to request "fr".
</p>