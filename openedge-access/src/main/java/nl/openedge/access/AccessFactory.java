package nl.openedge.access;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessControlException;
import java.security.Policy;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletContext;
import javax.sql.DataSource;

import nl.openedge.access.impl.rdbms.DataSourceDelegate;
import nl.openedge.access.impl.rdbms.RdbmsBase;
import nl.openedge.access.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * @author Hillenius
 * $Id$
 */
public class AccessFactory {

	/**
	 * Default location of the xml configuration file.
	 */
	public static final String DEFAULT_CONFIG_FILE = "/WEB-INF/oeaccess.xml";

	/**
	 * If a value is set in the application attribute context with this key,
	 * the value is used to override the setting of the configFile.
	 */
	public static final String KEY_CONFIG_FILE = "oeaccess.configFile";

	/**
	 * Name of the servlet init parameter which defines the path to the
	 * Maverick configuration file.  Defaults to DEFAULT_CONFIG_FILE.
	 */
	protected static final String INITPARAM_CONFIG_FILE = "oeaccess.configFile";

	/** save the reference if it is available */
	protected ServletContext servletContext = null;
	
	/** concrete user manager */
	protected UserManager userManager;
			
	/**
	 * configuration document
	 */	
	protected Document configuration;

	/** logger */
	private Log log = LogFactory.getLog(this.getClass());

	/**
	 * construct and initialise with configDocument
	 */
	public AccessFactory(String configDocument) throws ConfigException {
		
		this.configuration = loadConfigDocumentFromUrl(configDocument);
		internalInit();
	}
	
	/**
	 * construct and initialise with URL to configDocument
	 */
	public AccessFactory(URL configURL) throws ConfigException {
		
		this.configuration = loadConfigDocumentFromUrl(configURL);
		internalInit();
	}
	
	/**
	 * construct and initialise with servletContext
	 */
	public AccessFactory(ServletContext servletContext) throws ConfigException {
		
		// save reference for (possible) later use
		this.servletContext = servletContext;
		this.configuration = loadConfigDocumentInWebApp(servletContext);
		internalInit();
	}
	
	/* do 'real' initialisation */
	private void internalInit() throws ConfigException {
		
		Element root = configuration.getRootElement();	
		
		// allthough using a datasource dependends on the implementation,
		// it is probably that common that we'll try to load it is in the
		// configuration
		Element dsNode = root.getChild("datasource");
		if(dsNode != null) {
			String dataSourceRef = dsNode.getAttributeValue("reference");
			if(dataSourceRef != null) {
		
				try {
					Context ctx = new InitialContext();
					DataSource ds = (DataSource)ctx.lookup(dataSourceRef);
					RdbmsBase.setDataSource(ds);
					log.info("datasource loaded from " + dataSourceRef);
				} catch(Exception e) {
					throw new ConfigException(e);
				}
			} else {
				
				String delegate = dsNode.getAttributeValue("delegate");
				if(delegate == null) throw new ConfigException(
					"element datasource should have attribute 'reference' " +
					"that points to a datasource OR attribute 'delegate' " + 
					"with a delegate class that confirms to the interface " + 
					"nl.openedge.access.impl.rdbms.DataSourceDelegate.");
				
				try {
					Class cls = Class.forName(delegate);
					DataSourceDelegate d = (DataSourceDelegate)cls.newInstance();
					DataSource ds = d.getDataSource(XML.getParams(dsNode));
					if(ds == null) throw new ConfigException(
							"unable to load datasource from delegate " + d);
					RdbmsBase.setDataSource(ds);
					log.info("datasource loaded from delegate" + d);
					
				} catch(Exception e) {
					throw new ConfigException(e);
				}				
			}
					
		}	
		this.userManager = loadUserManager(root.getChild("user-manager"));
		
		// a client of this library does not have to configure the security
		// element. For instance, it could be configured in the JDK settings
		// directely instead
		Element securityNode = root.getChild("security");
		if(securityNode != null) {
			
			// add jaas config file (with LoginModule(s)) if attribute exists
			String jaasConfig = XML.getValue(securityNode, "jaas-config");
			if(jaasConfig != null) {
				try {
					URL jaasConfigURL = convertToURL(jaasConfig, servletContext);
					if(jaasConfigURL == null) throw new ConfigException(jaasConfig + " is not a valid url");
					String convertedJaasConfig = jaasConfigURL.toString();
					setLoginModule(convertedJaasConfig);
				} catch(Exception e) {
					throw new ConfigException(e);
				}
			}
			
			//	add policy file (with policies) if attribute exists
			String policyFile = XML.getValue(securityNode, "policies");
			if(jaasConfig != null) {
				try {
					URL policyURL = convertToURL(policyFile, servletContext);
					if(policyURL == null) throw new ConfigException(policyFile + " is not a valid url");
					String convertedPolicyURL = policyURL.toString();
					addPolicies(convertedPolicyURL);
				} catch(Exception e) {
					throw new ConfigException(e);
				}
			}
				
		}
	}
	
	/**
	 * add the configured LoginModule implementation to the list of LoginModules
	 * known by the security environment
	 * @param convertedJaasConfig
	 */
	protected void setLoginModule(String convertedJaasConfig) {

		boolean exists = false;
		int n = 1;
		String config_url;
		while ((config_url = java.security.Security.getProperty
						("login.config.url."+n)) != null) {		
			
			if(config_url.equalsIgnoreCase(convertedJaasConfig)) {
				exists = true;
				break;
			} 
			n++;
		}
		if(!exists) {
			String configKey = ("login.config.url."+n);
			java.security.Security.setProperty(configKey, convertedJaasConfig);
			log.info("added " + configKey + "=" +
							convertedJaasConfig + "to java.security.Security properties");
		}
				
	}
	
	/**
	 * add our own policy file to the security environment
	 * @param convertedPolicyURL
	 */
	protected void addPolicies(String convertedPolicyURL) {

		// handle the loading of Policies
		boolean exists = false;
		int n = 1;
		String policy_url;
		while ((policy_url = java.security.Security.getProperty
						("auth.policy.url."+n)) != null) {		

			if(policy_url.equalsIgnoreCase(convertedPolicyURL)) {
				exists = true;
				break;
			} 
			n++;
		}
		if(!exists) {
			String configKey = ("auth.policy.url."+n);
			java.security.Security.setProperty(configKey, convertedPolicyURL);
			log.info("added " + configKey + "=" +
					convertedPolicyURL + "to java.security.Security properties");
		}
		// reload the policy configuration to add our policies	
		try {
			Policy policy = Policy.getPolicy();
			policy.refresh();
		} catch (AccessControlException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * load and initialise access manager
	 * @param config
	 */
	protected UserManager loadUserManager(Element configNode) 
				throws ConfigException {
	
		UserManager manager = null;
		String managerCls = XML.getValue(configNode, "class");
		try {
			Class cls = Class.forName(managerCls);
			manager = (UserManager)cls.newInstance();
			manager.init(configNode);
			
			log.info(cls + " acting as UserManager");
						
		} catch(Exception e) {
			throw new ConfigException(e);
		}
		return manager;
	}

	/**
	 * @return a loaded JDOM document containing the configuration information.
	 */
	protected Document loadConfigDocumentFromUrl(String configDocument) 
				throws ConfigException {
					
		try {
			java.net.URL configURL = convertToURL(configDocument, null);
			if(configURL == null) throw new ConfigException(configDocument + 
					" should be a document but is empty");
			log.info("Loading config from " + configURL);
			
			return internalLoad(configURL);
			
		} catch (IOException ex) {
			throw new ConfigException(ex);
		}
	}
	
	/**
	 * @return a loaded JDOM document containing the configuration information.
	 */
	protected Document loadConfigDocumentFromUrl(URL configURL) 
				throws ConfigException {
					
		if(configURL == null) throw new ConfigException(configURL + 
				" should be a document but is empty");
		log.info("Loading config from " + configURL);
		
		return internalLoad(configURL);
	}
	
	/**
	 * @return a loaded JDOM document containing the configuration information.
	 */
	protected Document loadConfigDocumentInWebApp(ServletContext servletContext) 
				throws ConfigException {
					
		try	{
			String configFile = (String)servletContext.getAttribute(KEY_CONFIG_FILE);
			if (configFile == null)
				configFile = servletContext.getInitParameter(INITPARAM_CONFIG_FILE);
			if (configFile == null)
				configFile = DEFAULT_CONFIG_FILE;

			java.net.URL configURL = convertToURL(configFile, servletContext);
			if(configURL == null) throw new ConfigException(configFile + 
					" should be a document but is empty");
			log.info("Loading config from " + configURL.toString());

			return internalLoad(configURL);
			
		} catch (IOException ex) {
			throw new ConfigException(ex);
		}
	}
	
	/*
	 * @return a loaded JDOM document containing the configuration information.
	 */
	private Document internalLoad(URL configURL) throws ConfigException {
		try {
			
			log.info("Loading config from " + configURL.toString());

			try {
				SAXBuilder builder = new SAXBuilder();
				return builder.build(configURL.openStream(), configURL.toString());
			}
			catch (org.jdom.JDOMException jde) {
				
				throw new ConfigException(jde);
			}
		}
		catch (IOException ex) {
			
			throw new ConfigException(ex);
		}
	}
	
	/**
	 * Interprets some absolute URLs as external paths, otherwise generates URL
	 * appropriate for loading from internal webapp or, servletContext is null,
	 * loading from the classpath.
	 */
	protected URL convertToURL(String path, ServletContext servletContext) 
			throws MalformedURLException {
		
		if (path.startsWith("file:") || path.startsWith("http:") || 
				path.startsWith("https:") || path.startsWith("ftp:")) {
			return new URL(path);
		} else if(servletContext != null) {
			// Quick sanity check
			if (!path.startsWith("/"))
				path = "/" + path;
			return servletContext.getResource(path);
		} else {
			return getClass().getResource(path);			
		}
	}

	/**
	 * @return UserManager
	 */
	public UserManager getUserManager() {
		return userManager;
	}

}
