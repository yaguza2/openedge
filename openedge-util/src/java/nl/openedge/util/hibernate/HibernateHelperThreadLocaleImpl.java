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
package nl.openedge.util.hibernate;

import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.hibernate.FlushMode;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Interceptor;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.cfg.Configuration;

/**
 * Manages a hibernate session with ThreadLocal.
 *
 * @author Eelco Hillenius
 */
public class HibernateHelperThreadLocaleImpl implements HibernateHelperDelegate
{

	private static Log log = LogFactory.getLog(HibernateHelperThreadLocaleImpl.class);

	/**
	 * Holds the current hibernate session, if one has been created.
	 */
	protected static ThreadLocal hibernateHolder = new ThreadLocal();

	/**
	 * Hibernate session factory
	 */
	protected static SessionFactory factory;
	
	/**
	 * config url
	 */
	protected static URL configURL = null;
	
	/**
	 * Hibernate configuration object
	 */
	protected static Configuration configuration = null;
	
	/**
	 * The FlushMode for the Hibernate session default COMMIT,
	 * see Hibernate documentation for other values.
	 * 
	 */
	private static FlushMode flushMode = FlushMode.COMMIT;
	
	/**
	 * factory level interceptor class
	 */	
	protected static Class interceptorClass;
	protected static String interceptorClassName;
	
	/** 
	 * If true, only one instance will be created of the interceptor for all
	 * sessions, if false, a new - and thus thread safe - instance will be created
	 * for session.
	 */
	protected static boolean singleInterceptor;
	
	/** if singleInterceptor == true, this will be the instance that is used for all sessions */
	protected static Interceptor staticInterceptor;

	private static boolean wasInitialised = false;

	/**
	 * initialise
	 */
	public void init() throws Exception
	{

		if (!wasInitialised)
		{
			wasInitialised = true;
			// Initialize hibernate
			// configure; load mappings
			configuration = new Configuration();
			if(configURL != null)
			{
				configuration.configure(configURL);	
			}
			else
			{
				configuration.configure();	
			}
			// build a SessionFactory		
			factory = configuration.buildSessionFactory();
		}
	}

	/**
	 * Get session for this Thread
	 *
	 * @return an appropriate Session object
	 */
	public Session getSession() throws HibernateException
	{

		Session sess = (Session)hibernateHolder.get();
		if (sess == null && factory != null)
		{
			if(interceptorClass != null)
			{
				Interceptor interceptor = null;
				try
				{
					interceptor = getInterceptorInstance(interceptorClass);
				}
				catch (InstantiationException e)
				{
					log.error(e.getMessage(), e);
					throw new HibernateException(e);
				}
				catch (IllegalAccessException e)
				{
					log.error(e.getMessage(), e);
					throw new HibernateException(e);
				}
				sess = factory.openSession(interceptor);
			}
			else
			{
				sess = factory.openSession();
			}
			
			hibernateHolder.set(sess);
			sess.setFlushMode(flushMode);
		}
		
		return sess;
	}
	
	/**
	 * close session for this Thread
	 */
	public void closeSession() throws HibernateException
	{
		Session sess = (Session)hibernateHolder.get();
		if (sess != null)
		{
			hibernateHolder.set(null);
			try
			{
				sess.close();
			}
			catch (HibernateException ex)
			{
				log.error(ex);
			}
		}
	}
	
	/**
	 * disconnect session and remove from threadlocal for this Thread
	 */
	public void disconnectSession() throws HibernateException
	{
		Session sess = (Session)hibernateHolder.get();
		if (sess != null)
		{
			hibernateHolder.set(null);
			try
			{
				sess.disconnect();
			}
			catch (HibernateException ex)
			{
				log.error(ex);
			}
		}
	}
	
	/**
	 * set current session
	 * @param session hibernate session
	 * @param actionForCurrentSession one of the constants 
	 * 		HibernateHelperThreadLocaleImpl.ACTION_CLOSE close current session
	 * 		HibernateHelperThreadLocaleImpl.ACTION_DISCONNECT disconnect current session
	 */
	public void setSession(Session session, int actionForCurrentSession)
	{
		Session sess = (Session)hibernateHolder.get();
		if (sess != null)
		{
			if(actionForCurrentSession == HibernateHelper.ACTION_CLOSE)
			{
				try
				{
					sess.close();
				}
				catch (HibernateException ex)
				{
					log.error(ex);
				}	
			}
			else if(actionForCurrentSession == HibernateHelper.ACTION_DISCONNECT)
			{
				try
				{
					sess.disconnect();
				}
				catch (HibernateException ex)
				{
					log.error(ex);
				}				
			}
			else
			{
				throw new RuntimeException("invallid action " + actionForCurrentSession);
			}
		}
		hibernateHolder.set(session);		
	}

	/**
	 * @return the hibernate session factory
	 */
	public SessionFactory getSessionFactory()
	{
		return factory;
	}
	/**
	 * @param factory
	 */
	public void setSessionFactory(SessionFactory factory)
	{
		HibernateHelperThreadLocaleImpl.factory = factory;
	}

	/**
	 * @return URL
	 */
	public URL getConfigURL()
	{
		return configURL;
	}

	/**
	 * @param url
	 */
	public void setConfigURL(URL url)
	{
		configURL = url;
	}

	/**
	 * @return
	 */
	public Configuration getConfiguration()
	{
		return configuration;
	}
	
	/**
	 * Sets the FlushMode used for the session. Default FlushMode.COMMIT
	 * See the Hibernate documentation on different FlushModes.
	 * @param mode the new FlushMode.
	 */
	public void setFlushMode(FlushMode mode)
	{
		flushMode = mode;
	}
	
	/**
	 * Return the current FlushMode.
	 * @return
	 */
	public FlushMode getFlushMode()
	{
		return flushMode;
	}

	/**
	 * get factory level interceptor class name
	 * @return String factory level interceptor class name
	 */
	public String getInterceptorClass()
	{
		return interceptorClassName;
	}

	/**
	 * set factory level interceptor class name
	 * @param interceptor factory level interceptor class name
	 */
	public void setInterceptorClass(String className)
	{
		// reset
		interceptorClassName = null;
		interceptorClass = null;
		staticInterceptor = null;
		
		// try first
		Interceptor instance = null;
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if (classLoader == null)
		{
			classLoader = HibernateHelperThreadLocaleImpl.class.getClassLoader();
		}
		Class clazz = null;
		try
		{
			clazz = classLoader.loadClass(className);
			
			instance = getInterceptorInstance(clazz);
			
			log.info("set hibernate interceptor to " + className + 
				"; singleInterceptor == " + singleInterceptor);
		}
		catch (Exception e)
		{
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
		
		interceptorClassName = className;
		interceptorClass = clazz;
	}
	
	/**
	 * create a new instance of the interceptor with the provided class or,
	 * if singleInterceptor == true, the singleton instance (which will be created
	 * if it did not yet exist).
	 * @param clazz class of interceptor
	 * @return Interceptor new or singleton instance of Interceptor
	 */
	protected Interceptor getInterceptorInstance(Class clazz) 
		throws InstantiationException, 
		IllegalAccessException
	{
		if(singleInterceptor)
		{
			synchronized(HibernateHelperThreadLocaleImpl.class)
			{
				if(staticInterceptor == null)
				{
					staticInterceptor = (Interceptor)clazz.newInstance();		
				}
				return staticInterceptor;
			}
		}
		else
		{
			return (Interceptor)clazz.newInstance();	
		}
	}

	/**
	 * If true, only one instance will be created of the interceptor for all
	 * sessions, if false, a new - and thus thread safe - instance will be created
	 * for session.
	 * @return boolean
	 */
	public boolean isSingleInterceptor()
	{
		return singleInterceptor;
	}

	/**
	 * If true, only one instance will be created of the interceptor for all
	 * sessions, if false, a new - and thus thread safe - instance will be created
	 * for session.
	 * @param b
	 */
	public void setSingleInterceptor(boolean b)
	{
		singleInterceptor = b;
	}

}
