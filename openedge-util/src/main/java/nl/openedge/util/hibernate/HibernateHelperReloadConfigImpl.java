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
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.classic.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * NB: Deze class niet gebruiken: bij enig gebruik leidt dat tot OutOfMemoryErrors.
 * HibernateHelper geeft toegang tot Hibernate functionaliteit.
 */
@Deprecated
public class HibernateHelperReloadConfigImpl implements HibernateHelperDelegate
{
	/**
	 * De standaard flush mode waarmee sessies worden geopend.
	 */
	private static FlushMode flushMode = FlushMode.COMMIT;

	/**
	 * Gebruikt voor logging.
	 */
	private static Logger log = LoggerFactory.getLogger(HibernateHelperReloadConfigImpl.class);

	/**
	 * URL Hibernate configuratie.
	 */
	private static URL hibernateConfigURL = null;

	/**
	 * huidige factory voor Thread.
	 */
	protected static ThreadLocal<SessionFactory> factoryHolder = new ThreadLocal<SessionFactory>();

	/**
	 * huidige session voor Thread.
	 */
	protected static ThreadLocal<Session> sessionHolder = new ThreadLocal<Session>();

	/**
	 * Set FlushMode voor sessies. Standaard is COMMIT Zie Hibernate documentatie voor
	 * uitleg FlushModes.
	 * 
	 * @param mode
	 *            the new FlushMode.
	 */
	public static void setFlushMode(final FlushMode mode)
	{
		flushMode = mode;
	}

	/**
	 * Get the current FlushMode.
	 * 
	 * @return FlushMode
	 */
	public static FlushMode getFlushMode()
	{
		return flushMode;
	}

	/**
	 * @see nl.openedge.util.hibernate.HibernateHelperDelegate#init()
	 */
	@Override
	public void init()
	{
		// test een keer
		getSessionFactory();
	}

	/**
	 * @return @see nl.openedge.util.hibernate.HibernateHelperDelegate#getSessionFactory()
	 */
	@Override
	public SessionFactory getSessionFactory()
	{
		log.trace("Enter");
		SessionFactory factory = null;
		try
		{
			Configuration config = getConfiguration();
			factory = config.buildSessionFactory();
		}
		catch (HibernateException e)
		{
			log.error("Kan geen connectie maken", e);
		}
		log.trace("Leave");
		return factory;
	}

	/**
	 * @return URL
	 * @see nl.openedge.util.hibernate.HibernateHelperDelegate#getConfigURL()
	 */
	@Override
	public URL getConfigURL()
	{
		if (hibernateConfigURL == null)
		{
			log.info("geen configuratie voor Hibernate gegeven; gebruik /hibernate.cfg.xml");
			hibernateConfigURL =
				HibernateHelperReloadConfigImpl.class.getResource("/hibernate.cfg.xml");
		}
		return hibernateConfigURL;
	}

	/**
	 * @param url
	 * @see nl.openedge.util.hibernate.HibernateHelperDelegate#setConfigURL(java.net.URL)
	 */
	@Override
	public void setConfigURL(final URL url)
	{
		hibernateConfigURL = url;
	}

	/**
	 * @return @see nl.openedge.util.hibernate.HibernateHelperDelegate#getConfiguration()
	 */
	@Override
	public Configuration getConfiguration()
	{
		try
		{
			Configuration config = new Configuration();
			config.configure(getConfigURL());
			return config;
		}
		catch (HibernateException e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * @return @throws HibernateException
	 * @see nl.openedge.util.hibernate.HibernateHelperDelegate#getSession()
	 */
	@Override
	public Session getSession() throws HibernateException
	{
		return openSession();
	}

	/**
	 * Open nieuwe session factory.
	 * 
	 * @return Session Hibernate session
	 * @throws HibernateException
	 *             indien Hibernate geen sessie kan openen
	 */
	protected Session openSession() throws HibernateException
	{
		// voor het geval er nog iets is blijven hangen...
		closeSession();
		// nu het openen, en sla direct op in threadlocale voor sessions
		Session session = getSessionFactory().openSession();
		session.setFlushMode(flushMode);
		sessionHolder.set(session);
		return session;
	}

	/**
	 * Sluit sessie.
	 * 
	 * @throws HibernateException
	 * @see nl.openedge.util.hibernate.HibernateHelperDelegate#closeSession()
	 */
	@Override
	public void closeSession() throws HibernateException
	{
		Session session = sessionHolder.get();
		if (session != null)
		{
			session.close();
		}
	}

	/**
	 * Sluit huidige session factory.
	 * 
	 * @throws HibernateException
	 *             indien Hibernate de factory niet kan sluiten
	 */
	protected void closeFactory() throws HibernateException
	{
		SessionFactory sf = factoryHolder.get();
		if (sf != null)
		{
			sf.close();
		}
	}

	/**
	 * Sluit huidige session en session factory.
	 * 
	 * @throws HibernateException
	 *             indien Hibernate de sessie en/ of factory niet kan sluiten
	 */
	protected void closeResources() throws HibernateException
	{
		closeSession();
		closeFactory();
	}

	/**
	 * Open nieuwe session factory.
	 * 
	 * @param config
	 *            Configuratie object
	 * @throws HibernateException
	 *             indien Hibernate geen factory kan openen
	 */
	protected void openFactory(final Configuration config) throws HibernateException
	{
		// voor het geval er nog iets is blijven hangen...
		closeFactory();
		// nu het openen, en sla direct op in threadlocale voor factories
		SessionFactory sf = config.buildSessionFactory();
		factoryHolder.set(sf);
	}

	/**
	 * Koppel sessie los van de onderliggende JDBC connectie.
	 * 
	 * @throws HibernateException
	 *             indien disconnect bij Hibernate mislukt
	 * @see nl.openedge.util.hibernate.HibernateHelperDelegate#disconnectSession()
	 */
	@Override
	public void disconnectSession() throws HibernateException
	{
		Session session = sessionHolder.get();
		if (session != null)
		{
			session.disconnect();
		}
	}

	@Override
	public void setSession(final Session session, final int actionForCurrentSession)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void setSessionFactory(final SessionFactory factory)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * @see nl.openedge.util.hibernate.HibernateHelperDelegate#getInterceptorClass()
	 */
	@Override
	public String getInterceptorClass()
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * @see nl.openedge.util.hibernate.HibernateHelperDelegate#setInterceptorClass(java.lang.String)
	 */
	@Override
	public void setInterceptorClass(final String className)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * @see nl.openedge.util.hibernate.HibernateHelperDelegate#isSingleInterceptor()
	 */
	@Override
	public boolean isSingleInterceptor()
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * @see nl.openedge.util.hibernate.HibernateHelperDelegate#setSingleInterceptor(boolean)
	 */
	@Override
	public void setSingleInterceptor(final boolean b)
	{
		throw new UnsupportedOperationException();
	}

}
