/*
 * $Header$
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
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletContext;

import nl.openedge.util.UUIDHexGenerator;
import nl.openedge.util.config.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;

/**
 * <p>Clients of the ModuleFactory should either construct the factory with an
 * instance of <code>javax.servlet.ServletContext</code> or with an instance
 * of <code>java.lang.String</code>. The first is for usage in a web application
 * environment and tries to read the location of the configuration document from
 * <code>javax.servlet.ServletContext.getInitParameter("oeaccess.configFile")</code> 
 * Moreover, all other references to documents (e.g. jaas.config) in the
 * configuration file will be looked up relative to the context path of the web
 * application. The second case tries to load all files from the classpath. To
 * overide this behaviour you can specify url's in the configuration document,
 * e.g: file://c:/mywinboxdrive/mydir/mymodules.xml. A third option is to load 
 * the configuration document from a custom location. This is done by 
 * constructing the URL yourself and constructing the ModuleFactory
 * with this URL.
 * <p>In a web application environment, the constructed instance of this 
 * <code>ModuleFactory</code> will be saved in the <code>ServletContext</code>
 * under key 'oemodules.configFile'. 
 * 
 * @author Eelco Hillenius
 */
public class Configurator {

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
	private Log log = LogFactory.getLog(Configurator.class);
	
	// only one of the following three is set at a time
	/** pointer to url */
	protected URL configURL = null;
	/** pointer to servlet context */
	protected ServletContext servletContext = null;
	/** pointer to document location as a string */
	protected String configDocument = null;
	/** uuid generator */
	private UUIDHexGenerator uuidgenerator = new UUIDHexGenerator();

	/**
	 * construct and initialise with configDocument
	 * @param configDocument location of document as a string
	 */
	public Configurator(String configDocument) throws ConfigException {
		
		this.configDocument = configDocument;
		reload(configDocument);
	}
	
	/**
	 * construct and initialise with URL to configDocument
	 * @param configURL location of document as an URL
	 */
	public Configurator(URL configURL) throws ConfigException {
		
		this.configURL = configURL;
		reload(configURL);
	}
	
	/**
	 * construct and initialise with servletContext
	 * @param servletContext servlet context of webapplication
	 */
	public Configurator(ServletContext servletContext) throws ConfigException {

		this.servletContext = servletContext;
		reload(servletContext);
	}
	
	/**
	 * create and register the factories
	 * @param configuration
	 * @throws ConfigException
	 */
	protected void createFactories(
						Document configuration, 
						ServletContext servletContext) 
						throws ConfigException {
		
		Element root = configuration.getRootElement();
		Properties properties = getGlobalProperties(root);
		List factories = root.getChildren("factory");
		
		for(Iterator i = factories.iterator(); i.hasNext(); ) {
			
			Element factoryNode = (Element)i.next();
			String name = factoryNode.getAttributeValue("name");
			if(name == null) {
				throw new ConfigException("factory must have a name");
			}
			log.info("instantiating factory '" + name + "'");
			ModuleFactory moduleFactory = new ModuleFactoryImpl(
					name, properties, factoryNode, servletContext);
		}
	}
	
	/**
	 * read the properties from the root element
	 * @param root root element of config document
	 * @return Properties filled properties, possibly empty
	 */
	protected Properties getGlobalProperties(Element root) {
		
		Properties properties = new Properties();
		Element globalPropertiesNode = root.getChild("globalProperties");
		if(globalPropertiesNode != null) {
		
			List pList = globalPropertiesNode.getChildren("property");
			if(pList != null) for(Iterator j = pList.iterator(); j.hasNext(); ) {
					
				Element pElement = (Element)j.next();
				properties.put(pElement.getAttributeValue("name"),
							   pElement.getAttributeValue("value"));
			}
		}
		return properties;
	}
	
	/**
	 * reload the configuration this is known by this configurator
	 * @throws ConfigException
	 */
	public void reload() throws ConfigException {
		
		Document configuration = null;
		if(configURL != null) {
			reload(configURL);	
		} else if(servletContext != null) {
			reload(servletContext);	
		} else if(configDocument != null) {
			reload(configDocument);	
		}
	}
	
	/** construct and initialise with configDocument
	 * @param configDocument
	 * @return Document
	 */
	protected void reload(String configDocument) throws ConfigException {
		
		Document configuration = 
			DocumentLoader.loadDocumentFromUrl(configDocument);
		createFactories(configuration, null);
	}
	
	/**
	 * load config document with URL to configDocument
	 * @param configURL
	 * @return Document
	 */
	protected void reload(URL configURL) throws ConfigException {
		
		Document configuration = 
			DocumentLoader.loadDocumentFromUrl(configURL);
		createFactories(configuration, null);
	}
	
	/**
	 * initialise with servletContext
	 * @param servletContext
	 * @return Document
	 */
	protected void reload(ServletContext servletContext) throws ConfigException {

		String configFile = (String)servletContext.getAttribute(KEY_CONFIG_FILE);
		if (configFile == null)
			configFile = servletContext.getInitParameter(INITPARAM_CONFIG_FILE);
		if (configFile == null)
			configFile = DEFAULT_CONFIG_FILE;
		Document configuration = 
			DocumentLoader.loadDocumentInWebApp(configFile, servletContext);
		createFactories(configuration, servletContext);
	}

}