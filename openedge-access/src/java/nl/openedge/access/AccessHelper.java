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
package nl.openedge.access;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;

import java.security.Policy;
import java.util.Properties;

import javax.security.auth.Subject;
import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * The AccessHelper constructs and initialises objects that are used within
 * the autorisation framework. 
 * 
 * <p>The AccessHelper can be initialised in several environments; in effect 
 * a web application environment and a standalone environment.
 * 
 * <p>Clients of the AccessHelper should either construct the factory with an
 * instance of <code>javax.servlet.ServletContext</code> or with an instance
 * of <code>java.lang.String</code>. The first is for usage in a web application
 * environment and tries to read the location of the configuration document from
 * <code>javax.servlet.ServletContext.getInitParameter("oeaccess.configFile")</code> 
 * Moreover, all other references to documents (e.g. jaas.config) in the
 * configuration file will be looked up relative to the context path of the web
 * application. The second case tries to load all files from the classpath. To
 * overide this behaviour you can specify url's in the configuration document,
 * e.g: file://c:/mywinboxdrive/mydir/jaas.config. A third option is to load 
 * the configuration document from a custom location. This is done by 
 * constructing the URL yourself and constructing the AccessHelper
 * with this URL.
 * <p>In a web application environment, the constructed instance of this 
 * <code>AccessHelper</code> will be saved in the <code>ServletContext</code>
 * under key 'oeaccess.configFile'. 
 * 
 * @author Eelco Hillenius
 */
public final class AccessHelper
{

	/**
	 * Default location of the xml configuration file.
	 */
	public static final String DEFAULT_CONFIG_FILE = 
							"/WEB-INF/oeaccess.properties";

	/**
	 * If a value is set in the application attribute context with this key,
	 * the value is used to override the setting of the configFile.
	 */
	public static final String KEY_CONFIG_FILE = "oeaccess.configFile";

	/**
	 * Name of the servlet init parameter which defines the path to the
	 * OpenEdge Access configuration file.  Defaults to DEFAULT_CONFIG_FILE.
	 */
	public static final String INITPARAM_CONFIG_FILE = "oeaccess.configFile";

	/** logger */
	private static Log log = LogFactory.getLog(AccessHelper.class);

	/**
	 * hidden constructor. Clients should use the static methods instead
	 */
	protected AccessHelper()
	{
		//noop	
	}
	
	/**
	 * Check permission for subject. 
	 * Use this method to allways have the same protection domain
	 * @param permission permission to check
	 * @param subject subject to check permission for
	 */
	public static void checkPermissionForSubject(Permission permission, Subject subject)
	{
		AccessAction action = new AccessAction(permission);
		Subject.doAsPrivileged(subject, action, null);
		// if we get here and return normally, the user was authorised
		// otherwise a security exception will be thrown	
	}

	/**
	 * construct and initialise with URL to configDocument
	 * @param configURL url to configuration
	 * @param applicationname logical name of the application. 
	 * 	NOTE: FOR EACH APPLICATION IN THE SAME VM (IN PARTICULAR WEBAPPS) USE
	 * 		A UNIQUE NAME FOR PROPERTY appName OR YOU CAN HAVE CONFLICTS.
	 * 		ALSO, YOU CAN - AND ARE WISE TO DO SO - USE THIS NAME AS A SUBSTITUTE
	 * 		IN YOUR POLICY FILE, LIKE: codeBase "${oeaccess.appName}".
	 * 		THE GIVEN APPNAME WILL BE SET AS A SYSTEM PROPERTY IN THE FORM:
	 * 		"oeaccess.codesource." + appName
	 * @param configDocument
	 * @throws ConfigException
	 */
	public static void reload(String configDocument, String applicationname) 
		throws ConfigException
	{
		setAppNameSystemProperty(applicationname);
		Properties configuration = loadConfigFromUrl(configDocument);
		internalInit(configuration, null);
	}
	
	/**
	 * construct and initialise with URL to configDocument 
	 * and get the appName from properties with key applicationname
	 * @param configURL url to configuration
	 * @param configDocument
	 * @throws ConfigException
	 */
	public static void reload(String configDocument) 
		throws ConfigException
	{
		Properties configuration = loadConfigFromUrl(configDocument);
		setAppNameSystemProperty(configuration.getProperty("applicationname"));	
		internalInit(configuration, null);
	}

	/**
	 * construct and initialise with URL to configDocument
	 * @param configURL url to configuration
	 * @param applicationname logical name of the application. 
	 * 	NOTE: FOR EACH APPLICATION IN THE SAME VM (IN PARTICULAR WEBAPPS) USE
	 * 		A UNIQUE NAME FOR PROPERTY appName OR YOU CAN HAVE CONFLICTS.
	 * 		ALSO, YOU CAN - AND ARE WISE TO DO SO - USE THIS NAME AS A SUBSTITUTE
	 * 		IN YOUR POLICY FILE, LIKE: codeBase "${oeaccess.appName}".
	 * 		THE GIVEN APPNAME WILL BE SET AS A SYSTEM PROPERTY IN THE FORM:
	 * 		"oeaccess.codesource." + appName
	 * @throws ConfigException
	 */
	public static void reload(URL configURL, String applicationname) throws ConfigException
	{
		setAppNameSystemProperty(applicationname);
		Properties configuration = loadConfigFromUrl(configURL);
		internalInit(configuration, null);
	}
	
	/**
	 * construct and initialise with URL to configDocument
	 * and get the appName from properties with key applicationname
	 * @param configURL url to configuration
	 * @throws ConfigException
	 */
	public static void reload(URL configURL) throws ConfigException
	{
		Properties configuration = loadConfigFromUrl(configURL);
		setAppNameSystemProperty(configuration.getProperty("applicationname"));
		internalInit(configuration, null);
	}

	/**
	 * construct and initialise within a servlet context
	 * @param configURL url to configuration
	 * @param applicationname logical name of the application. 
	 * 	NOTE: FOR EACH APPLICATION IN THE SAME VM (IN PARTICULAR WEBAPPS) USE
	 * 		A UNIQUE NAME FOR PROPERTY appName OR YOU CAN HAVE CONFLICTS.
	 * 		ALSO, YOU CAN - AND ARE WISE TO DO SO - USE THIS NAME AS A SUBSTITUTE
	 * 		IN YOUR POLICY FILE, LIKE: codeBase "${oeaccess.codesource.appName}".
	 * 		THE GIVEN APPNAME WILL BE SET AS A SYSTEM PROPERTY IN THE FORM:
	 * 		"oeaccess.codesource." + appName
	 * @param servletContext
	 * @throws ConfigException
	 */
	public static void reload(ServletContext servletContext, String applicationname) 
		throws ConfigException
	{
		setAppNameSystemProperty(applicationname);
		Properties configuration = loadConfigInWebApp(servletContext);
		internalInit(configuration, servletContext);
	}
	
	/**
	 * construct and initialise within a servlet context
	 * and get the appName from properties with key applicationname
	 * @param configURL url to configuration
	 * @param servletContext
	 * @throws ConfigException
	 */
	public static void reload(ServletContext servletContext) 
		throws ConfigException
	{
		Properties configuration = loadConfigInWebApp(servletContext);
		setAppNameSystemProperty(configuration.getProperty("applicationname"));
		internalInit(configuration, servletContext);
	}
	
	/**
	 * set codesource of this instance of openedge-access as system property in form:
	 * "oeaccess.codesource." + applicationname
	 * @param appName logical application name
	 * @throws ConfigException
	 */
	protected static void setAppNameSystemProperty(String applicationname) 
		throws ConfigException
	{
		if(applicationname != null)
		{
			String key = "oeaccess.codesource." + applicationname;
			String check = System.getProperty(applicationname);	
			if(check != null)
			{
				throw new ConfigException(
					"application " + key + " was allready defined in VM");
			}
			ProtectionDomain pd = AccessHelper.class.getProtectionDomain();
			CodeSource cs = pd.getCodeSource();
			
			if(cs != null && (cs.getLocation() != null))
			{
				URL csurl = cs.getLocation();
				System.setProperty(key, csurl.toString());
				log.info("setting applicationname to " + applicationname +
					"; " + key + " will be expanded to " + csurl);	
			}
			else
			{
				log.warn("CodeSource could not be appointed. This makes " +
					"substitution in the policy file impossible, and thus it is not safe " +
					"to use openedge-access for more than one webapp in this server/ VM!");
			}	
		}
	}

	/* do 'real' initialisation */
	private static void internalInit(
		Properties configuration,
		ServletContext servletContext) 
		throws ConfigException
	{

		// a client of this library does not have to configure the security
		// element. For instance, it could be configured in the JDK settings
		// directely instead

		// add jaas config file (with LoginModule(s)) if property exists
		String jaasConfig = configuration.getProperty("jaas-config");
		if (jaasConfig != null)
		{
			try
			{
				URL jaasConfigURL = convertToURL(jaasConfig, servletContext);
				if (jaasConfigURL == null)
					throw new ConfigException(jaasConfig + " is not a valid url");
				String convertedJaasConfig = jaasConfigURL.toString();
				addLoginModule(convertedJaasConfig);
			}
			catch (Exception e)
			{
				throw new ConfigException(e);
			}
		}

		//	add policy file (with policies) if property exists
		String policyFile = configuration.getProperty("policies");
		if (policyFile != null)
		{
			try
			{
				URL policyURL = convertToURL(policyFile, servletContext);
				if (policyURL == null)
					throw new ConfigException(policyFile + " is not a valid url");
				String convertedPolicyURL = policyURL.toString();
				addPolicies(convertedPolicyURL);
			}
			catch (Exception e)
			{
				throw new ConfigException(e);
			}
		}

	}

	/**
	 * add the configured LoginModule implementation to the list of LoginModules
	 * known by the security environment
	 * @param convertedJaasConfig
	 */
	protected static void addLoginModule(String convertedJaasConfig)
	{

		boolean exists = false;
		int n = 1;
		String config_url;
		while ((config_url = java.security.Security.getProperty(
					"login.config.url." + n)) != null)
		{

			if (config_url.equalsIgnoreCase(convertedJaasConfig))
			{
				exists = true;
				
				log.warn("login url " + convertedJaasConfig + 
						 " is allready in the security environmoment (element " +
						 n + ")");
				
				break;
			}
			n++;
		}
		if (!exists)
		{
			String configKey = ("login.config.url." + n);
			java.security.Security.setProperty(configKey, convertedJaasConfig);
			log.info("added " + configKey + "=" + convertedJaasConfig 
				+ " to java.security.Security properties");
				
		}

	}

	/**
	 * add our own policy file to the security environment
	 * @param convertedPolicyURL
	 */
	protected static void addPolicies(String convertedPolicyURL)
	{

		// handle the loading of Policies
		boolean exists = false;
		int n = 1;
		String policy_url;
		while ((policy_url = java.security.Security.getProperty(
					"auth.policy.url." + n)) != null)
		{

			if (policy_url.equalsIgnoreCase(convertedPolicyURL))
			{
				exists = true;
				log.warn("policy url " + convertedPolicyURL + 
						 " allready is in the security environmoment (element " +
						 n + ")");
				break;
			}
			n++;
		}
		if (!exists)
		{
			String configKey = ("auth.policy.url." + n);
			java.security.Security.setProperty(configKey, convertedPolicyURL);
			log.info("added " + configKey + "=" + convertedPolicyURL 
				+ " to java.security.Security properties");
		}
		// reload the policy configuration to add our policies	
		try
		{
			Policy policy = Policy.getPolicy();
			
			log.info("refreshing Policy");
			policy.refresh();

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * load Properties
	 * @return a loaded Properties object containing 
	 * the configuration information.
	 */
	protected static Properties loadConfigFromUrl(String configDocument) 
		throws ConfigException
	{

		try
		{
			java.net.URL configURL = convertToURL(configDocument, null);
			if (configURL == null)
				throw new ConfigException(configDocument + 
						" should be a document but is empty");
			log.info("Loading config from " + configURL);

			return internalLoad(configURL);

		}
		catch (IOException ex)
		{
			throw new ConfigException(ex);
		}
	}

	/**
	 * @return a loaded Properties object containing the configuration information.
	 */
	protected static Properties loadConfigFromUrl(URL configURL) 
		throws ConfigException
	{

		if (configURL == null)
			throw new ConfigException(configURL 
				+ " should be a document but is empty");
		log.info("Loading config from " + configURL);

		return internalLoad(configURL);
	}

	/**
	 * @return a loaded Properties object containing the configuration information.
	 */
	protected static Properties loadConfigInWebApp(ServletContext servletContext) 
		throws ConfigException
	{

		try
		{
			String configFile = (String)servletContext.getAttribute(KEY_CONFIG_FILE);
			if (configFile == null)
				configFile = servletContext.getInitParameter(INITPARAM_CONFIG_FILE);
			if (configFile == null)
				configFile = DEFAULT_CONFIG_FILE;

			java.net.URL configURL = convertToURL(configFile, servletContext);
			if (configURL == null)
				throw new ConfigException(
					configFile + " should be a document but is empty");
			log.info("Loading config from " + configURL.toString());

			return internalLoad(configURL);

		}
		catch (IOException ex)
		{
			throw new ConfigException(ex);
		}
	}

	/*
	 * @return a loaded JDOM document containing the configuration information.
	 */
	private static Properties internalLoad(URL configURL) throws ConfigException
	{
		try
		{
			Properties p = new Properties();
			p.load(configURL.openStream());
			return p;
		}
		catch (Exception jde)
		{

			throw new ConfigException(jde);
		}
	}

	/**
	 * Interprets some absolute URLs as external paths, otherwise generates URL
	 * appropriate for loading from internal webapp or, servletContext is null,
	 * loading from the classpath.
	 */
	protected static URL convertToURL(String path, ServletContext servletContext) 
		throws MalformedURLException
	{

		if (path.startsWith("file:")
			|| path.startsWith("http:")
			|| path.startsWith("https:")
			|| path.startsWith("ftp:"))
		{
			return new URL(path);
		}
		else if (servletContext != null)
		{
			// Quick sanity check
			if (!path.startsWith("/"))
				path = "/" + path;
			return servletContext.getResource(path);
		}
		else
		{
			return AccessHelper.class.getResource(path);
		}
	}
}

/** action for checking permissions for a specific subject */
class AccessAction implements PrivilegedAction
{

	// permission to check on
	private Permission permission;

	/** construct with uri */
	public AccessAction(Permission permission)
	{
		this.permission = permission;
	}

	/** run check */
	public Object run()
	{
		java.security.AccessController.checkPermission(permission);
		return null;
	}
}
