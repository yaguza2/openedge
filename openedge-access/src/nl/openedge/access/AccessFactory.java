package nl.openedge.access;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletContext;

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
	protected static final String INITPARAM_CONFIG_FILE = "configFile";

	/** concrete access manager */
	protected AccessManager accessManager;
	
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
	 * construct and initialise with servletContext
	 */
	public AccessFactory(ServletContext servletContext) throws ConfigException {
		
		this.configuration = loadConfigDocumentInWebApp(servletContext);
		internalInit();
	}
	
	/* do 'real' initialisation */
	private void internalInit() throws ConfigException {
		
		Element root = configuration.getRootElement();
		this.accessManager = loadAccessManager(root.getChild("access-manager"));
		this.userManager = loadUserManager(root.getChild("user-manager"));
	}
	
	/**
	 * load and initialise access manager
	 * @param config
	 */
	protected AccessManager loadAccessManager(Element configNode) 
				throws ConfigException {
	
		AccessManager manager = null;
		String managerCls = XML.getValue(configNode, "class");
		try {
			Class cls = Class.forName(managerCls);
			manager = (AccessManager)cls.newInstance();
			manager.init(configNode);
			this.accessManager = manager;
			log.info(cls + " acting as AccessManager");
						
		} catch(Exception e) {
			throw new ConfigException(e);
		}
		Element providerNode = configNode.getChild("access-provider");
		if(providerNode == null) throw new ConfigException(
				"acces-provider is a mandatory element of access-manager");
		
		String providerCls = XML.getValue(providerNode, "class");	
		try {
			Class cls = Class.forName(providerCls);
			AccessProvider provider = (AccessProvider)cls.newInstance();
			provider.init(providerNode);
			accessManager.setAccessProvider(provider);
			
			log.info(cls + " set as AccessProvider for AccessManager");
						
		} catch(Exception e) {
			throw new ConfigException(e);
		}
		return manager;
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
	 * @return AccessManager
	 */
	public AccessManager getAccessManager() {
		return accessManager;
	}

	/**
	 * @return UserManager
	 */
	public UserManager getUserManager() {
		return userManager;
	}

}
