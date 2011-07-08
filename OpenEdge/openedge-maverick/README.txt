
The Maverick MVC Framework

Version 2.2.2

This release of Maverick requires:
	JDK 1.2+
	JAXP 1.1
	Servlet API 2.3
	Several jars which have been included in the dist.  See the
	FAQ or the examples for a list of what is necessary.

In particular note that Maverick requires a web container which
supports the Servlet 2.3 API.  It is possible to get it working
with a 2.2 container, but with a much restricted feature set.

Maverick 2.2 is known to work with Tomcat 4, Jetty 4, Resin, and
Orion.  If anyone is using Maverick with another application server,
drop a line on the mav-user mailing list (mav-user@lists.sourceforge.net).
Maverick should work with any web container that supports the
Servlet 2.3 specification.

Note that Orion 1.5.4 has a broken ServletResponseWrapper which
is incompatible with Maverick (or the Serlvet specification).

Thanks,
Jeff Schnitzer
Scott Hernandez
Jim Moore