This sample demonstrates how to use a ShuntFactory to internationalize
a webapp using Maverick.  The LanguageShuntFactory automatically
selects view modes based on the Accept-Language header submitted by
the user agent.  Try changing the preferred language of your browser
to "fr" and the web pages will change.

ShuntFactories can be written to switch modes based on any
characteristics of the request, such as User-Agent or 
Accept.

----- Orion -----

To install the shunting example on Orion 1.4.8 (or later):

1.  Copy shunting-jsp.war into orion/applications.
2.  Copy lib/commons-logging-1.0.3.jar into orion/lib.
3.  Add this to the end of orion/config/application.xml:

	<web-module id="shunting" path="../applications/shunting-jsp.war" />

4.  Add this to orion/config/default-web-site.xml:

	<web-app application="default" name="shunting" root="/sh" />

When you start Orion, the example should autodeploy and be available
as /sh.

For previous versions of Orion, you will need to upgrade your
JAXP and XML parser jars.


----- Tomcat 4.0 -----

To install the shunting example on Tomcat 4.0:

1.  Copy shunting-jsp.war into tomcat-4.0/webapps.
2.  Copy lib/log4j.jar into tomcat-4.0/lib.
3.  If you are using JDK < 1.4, copy lib/xml-apis.jar into tomcat-4.0/lib.

When you start tomcat, the example should be available as /shunting-jsp.
