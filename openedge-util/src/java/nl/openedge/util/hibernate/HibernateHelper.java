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

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.cfg.Configuration;

/**
 * @author Eelco Hillenius
 */
public class HibernateHelper {
	
	private static HibernateHelperDelegate delegate = null;
	static
	{
		String filename = "/hibernatehelper.properties";
		Properties props = new Properties();
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
		{
			try
			{
				props.load(is);
				String delegateImpl = props.getProperty("delegate");
				if(delegateImpl != null)
				{
					Class clazz = loader.loadClass(delegateImpl);
					delegate = (HibernateHelperDelegate)clazz.newInstance();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		if(delegate == null)
		{
			System.err.println("fallback on default HibernateHelperDelegate implementation: " +
				HibernateHelperThreadLocaleImpl.class.getName());
			delegate = new HibernateHelperThreadLocaleImpl();
		}
	}
	
	
    /** close current session on setSession */
    public final static int ACTION_CLOSE = 1;

    /** disconnect current session on setSession */
    public final static int ACTION_DISCONNECT = 2;
    
	/**
	 * Initialise.
	 */
	public static void init() throws Exception
	{
		delegate.init();
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