/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Copyright (c) 2003, Open Edge B.V.
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, 
 * this list of conditions and the following disclaimer. Redistributions 
 * in binary form must reproduce the above copyright notice, this list of 
 * conditions and the following disclaimer in the documentation and/or other 
 * materials provided with the distribution. Neither the name of OpenEdge B.V. 
 * nor the names of its contributors may be used to endorse or promote products 
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package nl.openedge.modules;

import java.net.URL;

import javax.servlet.ServletContext;

import nl.openedge.modules.config.ConfigException;
import nl.openedge.modules.config.DocumentLoader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;

/**
 * <p>Clients of the ComponentRepository should either construct the factory with an
 * instance of <code>javax.servlet.ServletContext</code> or with an instance
 * of <code>java.lang.String</code>. The first is for usage within a web application
 * environment and tries to read the location of the configuration document from
 * <code>javax.servlet.ServletContext.getInitParameter("oemodules.configFile")</code> 
 * If you are not within a web application context, or if you want to load the 
 * configuration from a location outside the web application environment, you
 * can provide an URL as either a string or an URL object,
 * e.g: file://c:/mywinboxdrive/mydir/mycomponents.xml. If you provide a
 * string with a relative path, e.g: /mycomponents.xml, the configurator
 * tries to load from the classpath, where / is the classpath root.
 * 
 * For example, you could have a startup/ main servlet that does this:
 * 
 * 	public void init(ServletConfig config) throws ServletException
 *	{
 *		new JDOMConfigurator(config.getServletContext());
 *	}
 *
 * and have this in your web.xml:
 * 
 * 	&lt;servlet&gt;
 *		&lt;servlet-name&gt;Application&lt;/servlet-name&gt;
 *		&lt;servlet-class&gt;com.foo.bar.ApplicationServlet&lt;/servlet-class&gt;
 *		&lt;init-param&gt;
 *			&lt;param-name&gt;oemodules.configFile&lt;/param-name&gt;
 *			&lt;param-value&gt;WEB-INF/oemodules.xml&lt;/param-value&gt;
 *		&lt;/init-param&gt;
 *		&lt;load-on-startup&gt;1&lt;/load-on-startup&gt;
 *	&lt;/servlet&gt;
 * 
 * @author Eelco Hillenius
 */
public class JDOMConfigurator
{

	/**
	 * Default location of the xml configuration file.
	 */
	public static String DEFAULT_CONFIG_FILE = "/WEB-INF/oemodules.xml";

	/**
	 * If a value is set in the application attribute context with this key,
	 * the value is used to override the setting of the configFile.
	 */
	public static String KEY_CONFIG_FILE = "oemodules.configFile";

	/**
	 * Name of the servlet init parameter which defines the path to the
	 * OpenEdge Modules configuration file.  Defaults to DEFAULT_CONFIG_FILE.
	 */
	public static String INITPARAM_CONFIG_FILE = "oemodules.configFile";

	/* logger */
	private Log log = LogFactory.getLog(JDOMConfigurator.class);

	// only one of the following three is set at a time
	/** pointer to url */
	protected URL configURL = null;
	
	/** pointer to servlet context */
	protected ServletContext servletContext = null;
	
	/** pointer to document location as a string */
	protected String configDocument = null;

	/**
	 * construct and initialise with configDocument
	 * @param configDocument location of document as a string
	 */
	public JDOMConfigurator(String configDocument) throws ConfigException
	{
		this.configDocument = configDocument;
		reload(configDocument);
	}

	/**
	 * construct and initialise with URL to configDocument
	 * @param configURL location of document as an URL
	 */
	public JDOMConfigurator(URL configURL) throws ConfigException
	{

		this.configURL = configURL;
		reload(configURL);
	}

	/**
	 * construct and initialise with servletContext
	 * @param servletContext servlet context of webapplication
	 */
	public JDOMConfigurator(ServletContext servletContext) throws ConfigException
	{

		this.servletContext = servletContext;
		reload(servletContext);
	}

	/**
	 * create and register the component repository
	 * @param configuration
	 * @throws ConfigException
	 */
	protected void createRepository(Document configuration, 
			ServletContext servletContext) throws ConfigException
	{

		Element rootNode = configuration.getRootElement();
		RepositoryFactory.initialize(rootNode, servletContext);
	}

	/**
	 * reload the configuration this is known by this configurator
	 * @throws ConfigException
	 */
	public void reload() throws ConfigException
	{
		RepositoryFactory.reset();
		Document configuration = null;
		if (configURL != null)
		{
			reload(configURL);
		}
		else if (servletContext != null)
		{
			reload(servletContext);
		}
		else if (configDocument != null)
		{
			reload(configDocument);
		}
	}

	/** construct and initialise with configDocument
	 * @param configDocument
	 * @return Document
	 */
	protected void reload(String configDocument) throws ConfigException
	{
		Document configuration = DocumentLoader.loadDocumentFromUrl(configDocument);
		createRepository(configuration, null);
	}

	/**
	 * load config document with URL to configDocument
	 * @param configURL
	 * @return Document
	 */
	protected void reload(URL configURL) throws ConfigException
	{
		Document configuration = DocumentLoader.loadDocumentFromUrl(configURL);
		createRepository(configuration, null);
	}

	/**
	 * initialise with servletContext
	 * @param servletContext
	 * @return Document
	 */
	protected void reload(ServletContext servletContext) throws ConfigException
	{

		String configFile = (String)servletContext.getAttribute(KEY_CONFIG_FILE);
		if (configFile == null)
			configFile = servletContext.getInitParameter(INITPARAM_CONFIG_FILE);
		if (configFile == null)
			configFile = DEFAULT_CONFIG_FILE;
		Document configuration = DocumentLoader.loadDocumentInWebApp(
										configFile, servletContext);
		createRepository(configuration, servletContext);
	}

}
