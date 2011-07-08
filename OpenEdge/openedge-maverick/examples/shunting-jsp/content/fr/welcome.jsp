<%--
	$Id: welcome.jsp,v 1.1 2002/01/03 11:48:40 lhoriman Exp $
	$Source: /cvsroot-fuse/mav/maverick/examples/shunting-jsp/content/fr/welcome.jsp,v $
--%>

<% request.setAttribute("title", "Welcome"); %>

<p style="color: blue">
	Please pretend for a moment that this is written in french.
</p>

<p>
	This is a very simple web application which demostrates "shunting".
	Shunting allows you to attach modes to different views which have
	the same name, and Maverick will automatically pick the right view
	to render.  This example uses the LanguageShuntFactory, which
	uses the browser's Accept-Language header to determine mode.
</p>
