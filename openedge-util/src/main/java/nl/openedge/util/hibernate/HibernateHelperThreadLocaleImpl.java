/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ================================================================================
 * Copyright (c) 2004, Open Edge B.V.,  All Rights Reserved.
 */
package nl.openedge.util.hibernate;

import java.net.URL;

import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.classic.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages a hibernate session with ThreadLocal.
 * 
 * @author Eelco Hillenius
 */
public class HibernateHelperThreadLocaleImpl implements HibernateHelperDelegate
{
	/** Log. */
	private static Logger log = LoggerFactory.getLogger(HibernateHelperThreadLocaleImpl.class);

	/**
	 * The FlushMode for the Hibernate session default COMMIT, see Hibernate documentation
	 * for other values.
	 */
	private static FlushMode flushMode = FlushMode.COMMIT;

	/** whether this delegate is initialised yet. */
	private static boolean initialised = false;

	/**
	 * Holds the current hibernate session, if one has been created.
	 */
	private static ThreadLocal<Session> hibernateHolder = new ThreadLocal<Session>();

	/**
	 * Hibernate session factory.
	 */
	private static SessionFactory factory;

	/**
	 * config url.
	 */
	private static URL configURL = null;

	/**
	 * Hibernate configuration object.
	 */
	private static Configuration configuration = null;

	/**
	 * factory level interceptor class.
	 */
	private static Class< ? extends Interceptor> interceptorClass;

	/**
	 * class name of the interceptor.
	 */
	private static String interceptorClassName;

	/**
	 * If true, only one instance will be created of the interceptor for all sessions, if
	 * false, a new - and thus thread safe - instance will be created for session.
	 */
	private static boolean singleInterceptor;

	/**
	 * if singleInterceptor == true, this will be the instance that is used for all
	 * sessions.
	 */
	private static Interceptor staticInterceptor;

	/**
	 * initialise.
	 * 
	 * @throws ConfigException
	 *             when this delegate could not be properly initialized
	 */
	@Override
	public void init() throws ConfigException
	{

		if (!initialised)
		{
			initialised = true;
			// Initialize hibernate
			// configure; load mappings
			configuration = new Configuration();
			try
			{
				if (configURL != null)
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
			catch (HibernateException e)
			{
				log.error(e.getMessage(), e);
				throw new ConfigException(e);
			}
		}
	}

	/**
	 * Get session for this Thread.
	 * 
	 * @return an appropriate Session object
	 * @throws HibernateException
	 *             when an unexpected Hibernate exception occurs
	 */
	@Override
	public Session getSession() throws HibernateException
	{

		Session sess = hibernateHolder.get();
		if (sess == null && factory != null)
		{
			if (interceptorClass != null)
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
	 * close session for this Thread.
	 * 
	 * @throws HibernateException
	 *             when an unexpected Hibernate exception occurs
	 */
	@Override
	public void closeSession() throws HibernateException
	{
		Session sess = hibernateHolder.get();
		if (sess != null)
		{
			hibernateHolder.set(null);
			try
			{
				sess.close();
			}
			catch (HibernateException ex)
			{
				log.error(ex.getMessage(), ex);
			}
		}
	}

	/**
	 * disconnect session and remove from threadlocal for this Thread.
	 * 
	 * @throws HibernateException
	 *             when an unexpected Hibernate exception occurs
	 */
	@Override
	public void disconnectSession() throws HibernateException
	{
		Session sess = hibernateHolder.get();
		if (sess != null)
		{
			hibernateHolder.set(null);
			try
			{
				sess.disconnect();
			}
			catch (HibernateException ex)
			{
				log.error(ex.getMessage(), ex);
			}
		}
	}

	/**
	 * set current session.
	 * 
	 * @param session
	 *            hibernate session
	 * @param actionForCurrentSession
	 *            one of the constants HibernateHelperThreadLocaleImpl.ACTION_CLOSE close
	 *            current session HibernateHelperThreadLocaleImpl.ACTION_DISCONNECT
	 *            disconnect current session
	 */
	@Override
	public void setSession(Session session, int actionForCurrentSession)
	{
		Session sess = hibernateHolder.get();
		if (sess != null)
		{
			if (actionForCurrentSession == HibernateHelper.ACTION_CLOSE)
			{
				try
				{
					sess.close();
				}
				catch (HibernateException ex)
				{
					log.error(ex.getMessage(), ex);
				}
			}
			else if (actionForCurrentSession == HibernateHelper.ACTION_DISCONNECT)
			{
				try
				{
					sess.disconnect();
				}
				catch (HibernateException ex)
				{
					log.error(ex.getMessage(), ex);
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
	 * @see nl.openedge.util.hibernate.HibernateHelperDelegate#getSessionFactory()
	 */
	@Override
	public SessionFactory getSessionFactory()
	{
		return factory;
	}

	@Override
	public void setSessionFactory(SessionFactory theFactory)
	{
		factory = theFactory;
	}

	/**
	 * @see nl.openedge.util.hibernate.HibernateHelperDelegate#getConfigURL()
	 */
	@Override
	public URL getConfigURL()
	{
		return configURL;
	}

	/**
	 * @see nl.openedge.util.hibernate.HibernateHelperDelegate#setConfigURL(java.net.URL)
	 */
	@Override
	public void setConfigURL(URL url)
	{
		configURL = url;
	}

	/**
	 * @see nl.openedge.util.hibernate.HibernateHelperDelegate#getConfiguration()
	 */
	@Override
	public Configuration getConfiguration()
	{
		return configuration;
	}

	/**
	 * Sets the FlushMode used for the session. Default FlushMode.COMMIT See the Hibernate
	 * documentation on different FlushModes.
	 * 
	 * @param mode
	 *            the new FlushMode.
	 */
	public void setFlushMode(FlushMode mode)
	{
		flushMode = mode;
	}

	/**
	 * Return the current FlushMode.
	 * 
	 * @return FlushMode the current flush mode
	 */
	public FlushMode getFlushMode()
	{
		return flushMode;
	}

	/**
	 * get factory level interceptor class name.
	 * 
	 * @return String factory level interceptor class name
	 */
	@Override
	public String getInterceptorClass()
	{
		return interceptorClassName;
	}

	/**
	 * set factory level interceptor class name.
	 * 
	 * @param className
	 *            factory level interceptor class name
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void setInterceptorClass(String className)
	{
		// reset
		interceptorClassName = null;
		interceptorClass = null;
		staticInterceptor = null;

		// try first
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if (classLoader == null)
		{
			classLoader = HibernateHelperThreadLocaleImpl.class.getClassLoader();
		}
		Class< ? extends Interceptor> clazz = null;

		try
		{
			clazz = (Class< ? extends Interceptor>) classLoader.loadClass(className);
			getInterceptorInstance(clazz);
			log.info("set hibernate interceptor to " + className + "; singleInterceptor == "
				+ singleInterceptor);
		}
		catch (ClassNotFoundException e)
		{
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
		catch (InstantiationException e)
		{
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
		catch (IllegalAccessException e)
		{
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}

		interceptorClassName = className;
		interceptorClass = clazz;
	}

	/**
	 * create a new instance of the interceptor with the provided class or, if
	 * singleInterceptor == true, the singleton instance (which will be created if it did
	 * not yet exist).
	 * 
	 * @param clazz
	 *            class of interceptor
	 * @return Interceptor new or singleton instance of Interceptor
	 * @throws IllegalAccessException
	 *             see exc doc
	 * @throws InstantiationException
	 *             see exc doc
	 */
	protected Interceptor getInterceptorInstance(Class< ? extends Interceptor> clazz)
			throws InstantiationException, IllegalAccessException
	{
		if (singleInterceptor)
		{
			synchronized (HibernateHelperThreadLocaleImpl.class)
			{
				if (staticInterceptor == null)
				{
					staticInterceptor = clazz.newInstance();
				}
				return staticInterceptor;
			}
		}
		else
		{
			return clazz.newInstance();
		}
	}

	/**
	 * If true, only one instance will be created of the interceptor for all sessions, if
	 * false, a new - and thus thread safe - instance will be created for session.
	 * 
	 * @return boolean
	 */
	@Override
	public boolean isSingleInterceptor()
	{
		return singleInterceptor;
	}

	/**
	 * If true, only one instance will be created of the interceptor for all sessions, if
	 * false, a new - and thus thread safe - instance will be created for session.
	 * 
	 * @param b
	 *            If true, only one instance will be created of the interceptor for all
	 *            sessions, if false, a new - and thus thread safe - instance will be
	 *            created for session.
	 */
	@Override
	public void setSingleInterceptor(boolean b)
	{
		singleInterceptor = b;
	}

	/**
	 * Get hibernateHolder.
	 * 
	 * @return the hibernateHolder.
	 */
	public static ThreadLocal<Session> getHibernateHolder()
	{
		return hibernateHolder;
	}

	/**
	 * Set hibernateHolder.
	 * 
	 * @param hibernateHolder
	 *            hibernateHolder to set.
	 */
	public static void setHibernateHolder(ThreadLocal<Session> hibernateHolder)
	{
		HibernateHelperThreadLocaleImpl.hibernateHolder = hibernateHolder;
	}
}
