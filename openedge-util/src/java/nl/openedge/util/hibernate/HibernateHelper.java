/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Copyright (c) 2003
 * All rights reserved.
 */
package nl.openedge.util.hibernate;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.cfg.Configuration;
import nl.openedge.util.URLHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Helper class to be able to transparantly obtain and configure Hibernate sessions.
 * <p>
 * Before HibernateHelper can be used, HibernateHelper.init() should be called once.
 * After this, you can obtain the current session with HibernateHelper.getSession();
 * If you use the HibernateFiler from this package, you do not need to 
 * (or better you should never) close the session.
 * </p>
 * <p>
 * If you want to close the session, preferably use HibernateHelper.closeSession().
 * </p>
 * <p>
 * Some defaults can be overriden by:
 * </p>
 * <p>
 * providing hibernatehelper.properties in the classpath root with properties:
 * <ul>
 *  <li>
 *      delegate: the fully classified classname of the delegate implementation.
 *      The default implementation is nl.openedge.util.hibernate.HibernateHelperThreadLocaleImpl.
 *      To override, eg: 'delegate=nl.levob.util.hibernate.HibernateHelperReloadConfigImpl'. 
 *  </li>
 *  <li>
 *      hibernateConfig: the url of the hibernate configuration to use.
 *  </li>
 * </ul>
 * </p>
 * <p>
 * These overrides can be overriden by setting environment variables (like -Dfoo=bar).
 * 'hibernatehelper.properties.delegate' for the delegate, and
 * 'hibernatehelper.properties.hibernateConfig' for the hibernate configuration location.
 * </p>
 * <p>
 * Setting the config url like: HibernateHelper.setConfigURL(myUrl);
 * Note that this will override the hibernateConfig variable as well.
 * </p>
 * <p>
 * By default, the configuration is loaded from the file 'hibernate.cfg.xml' in the classpath root.
 * 
 * @author Eelco Hillenius
 */
public class HibernateHelper {
	
    // log
    private static Log log = LogFactory.getLog(HibernateHelper.class);

    /** close current session on setSession */
    public final static int ACTION_CLOSE = 1;

    /** disconnect current session on setSession */
    public final static int ACTION_DISCONNECT = 2;

    /**
     * key of the system property for setting the delegate.
     * value = hibernatehelper.properties.delegate
     */
    public final static String SYSTEM_PROPERTY_DELEGATE = 
        "hibernatehelper.properties.delegate";

    /**
     * key of the system property for setting the hibernate config.
     * value = hibernatehelper.properties.hibernateConfig
     */
    public final static String SYSTEM_PROPERTY_HIBERNATE_CONFIG = 
        "hibernatehelper.properties.hibernateConfig";

    /**
     * key of the property (from file) for setting the delegate.
     * value = delegate.
     */
    public final static String PROPERTY_DELEGATE = "delegate";

    /**
     * key of the property (from file) for setting the hibernate config.
     * value = hibernateConfig.
     */
    public final static String PROPERTY_HIBERNATE_CONFIG = "hibernateConfig";

    /**
     * filename of properties for HibernateHelper. value = /hibernatehelper.properties
     */
    public final static String PROPERTIES_LOCATION = "/hibernatehelper.properties";
    
    // the implementation delegate; does the 'real' work for this class.
	private static HibernateHelperDelegate delegate = null;
	static // initialize the helper
	{
	    initialize();
	}
    
	/**
	 * Initialise.
	 */
	public static void init() throws Exception
	{
		delegate.init();
	}

	/**
	 * Does initialization of the helper.
	 */
	private static void initialize()
	{
	    String filename = PROPERTIES_LOCATION;
	    // first, see if there are system properties defined
		Properties properties = new Properties();
		InputStream is = null;
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if (loader != null)
		{ // try to load with context class loader
			is = loader.getResourceAsStream(filename);
		}
		if ((loader == null) || (is == null))
		{ // classloader fallthrough
			is = HibernateHelper.class.getResourceAsStream(filename);
		}
		if(is != null)
		{ // if an inputstream was found, try to read properties object from it
			try
			{
                properties.load(is);
            }
			catch (IOException e)
			{
                log.error(e.getMessage(), e);
            }
		}
		// do further initialization
		initializeDelegate(properties);
		initializeHibernateConfig(properties);
	}

	/**
	 * Initialize the delegate.
	 * @param properties the properties
	 */
	private static void initializeDelegate(Properties properties)
	{
	    String delegateImplClass = System.getProperty(SYSTEM_PROPERTY_DELEGATE);
		if(delegateImplClass == null) // if not a system property
		{
		    delegateImplClass = properties.getProperty(PROPERTY_DELEGATE);    
		}
		if(delegateImplClass != null) // try to load the delegate
		{
		    try
		    {
		        ClassLoader loader = Thread.currentThread().getContextClassLoader();
		        if(loader == null)
		        {
		            loader = HibernateHelper.class.getClassLoader();
		        }
				Class clazz = loader.loadClass(delegateImplClass);
				delegate = (HibernateHelperDelegate)clazz.newInstance();
		    }
		    catch(Exception e)
		    {
		        log.error(e.getMessage(), e);
		    }
		}
		if(delegate == null) // if no delegate was given or instantiation failed
		{
			log.info("fallback on default HibernateHelperDelegate implementation: " +
				HibernateHelperThreadLocaleImpl.class.getName());
			delegate = new HibernateHelperThreadLocaleImpl();
		}    
	}

	/**
	 * initialize hibernate config.
	 * @param properties the properties
	 */
	private static void initializeHibernateConfig(Properties properties)
	{
	    String hibernateConfig = System.getProperty(SYSTEM_PROPERTY_HIBERNATE_CONFIG);
        // is the default configuration location overriden?
		if(hibernateConfig == null) // if not a system property
		{
		    hibernateConfig = properties.getProperty(PROPERTY_HIBERNATE_CONFIG);    
		}
		if(hibernateConfig != null)
		{
            log.info("using Hibernate config from URL: " + hibernateConfig);
            try
            {
                URL config = URLHelper.convertToURL(hibernateConfig, HibernateHelper.class);
                if(config != null)
                {
                    setConfigURL(config);   
                }
                else
                {
                    log.error("unable to construct URL from " + hibernateConfig);
                }
            }
            catch(Exception e)
            {
                log.error(e.getMessage(), e);
            }
		}		    
	}
	
	/**
	 * Get session for this Thread.
	 *
	 * @return an appropriate Session object
	 */
	public static Session getSession() throws HibernateException
	{
		return delegate.getSession();
	}
	
	/**
	 * Close session for this Thread.
	 */
	public static void closeSession() throws HibernateException
	{
		delegate.closeSession();
	}
	
	/**
	 * disconnect session and remove from threadlocal for this Thread.
	 */
	public static void disconnectSession() throws HibernateException
	{
		delegate.disconnectSession();
	}
	
	/**
	 * Set current session.
	 * @param session hibernate session
	 * @param actionForCurrentSession one of the constants 
	 * 		HibernateHelperThreadLocaleImpl.ACTION_CLOSE close current session
	 * 		HibernateHelperThreadLocaleImpl.ACTION_DISCONNECT disconnect current session
	 */
	public static void setSession(Session session, int actionForCurrentSession)
	{
		delegate.setSession(session, actionForCurrentSession);
	}

	/**
	 * Get the Hibernate session factory.
	 * 
	 * @return the hibernate session factory
	 */
	public static SessionFactory getSessionFactory()
	{
		return delegate.getSessionFactory();
	}
	
	/**
	 * Set the Hibernate session factory.
	 * 
	 * @param factory the Hibernate session factory
	 */
	public static void setSessionFactory(SessionFactory factory)
	{
		HibernateHelperThreadLocaleImpl.factory = factory;
	}

	/**
	 * Get the configuration URL.
	 * 
	 * @return URL the configuration URL
	 */
	public static URL getConfigURL()
	{
		return delegate.getConfigURL();
	}

	/**
	 * Set the configuration URL.
	 * 
	 * @param url the configuration URL
	 */
	public static void setConfigURL(URL url)
	{
        log.info("use config from " + url);
		delegate.setConfigURL(url);
	}

	/**
	 * Get the Hibernate configuration object.
	 * 
	 * @return Configuration
	 */
	public static Configuration getConfiguration()
	{
		return delegate.getConfiguration();
	}

	/**
	 * get factory level interceptor class name
	 * @return String factory level interceptor class name
	 */
	public static String getInterceptorClass()
	{
		return delegate.getInterceptorClass();
	}

	/**
	 * set factory level interceptor class name
	 * @param interceptor factory level interceptor class name
	 */
	public static void setInterceptorClass(String className)
	{
		delegate.setInterceptorClass(className);
	}

	/**
	 * If true, only one instance will be created of the interceptor for all
	 * sessions, if false, a new - and thus thread safe - instance will be created
	 * for session.
	 * @return boolean
	 */
	public static boolean isSingleInterceptor()
	{
		return delegate.isSingleInterceptor();
	}

	/**
	 * If true, only one instance will be created of the interceptor for all
	 * sessions, if false, a new - and thus thread safe - instance will be created
	 * for session.
	 * @param b
	 */
	public static void setSingleInterceptor(boolean b)
	{
		delegate.setSingleInterceptor(b);
	}


    /**
     * Get the concrete instance of helper delegate.
     * 
     * @return HibernateHelperDelegate the concrete instance of helper delegate
     */
    public static HibernateHelperDelegate getDelegate() {
        return delegate;
    }

}