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

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.cfg.Configuration;

/**
 * Manages a hibernate session with ThreadLocal.
 *
 * @author Eelco Hillenius
 */
public abstract class HibernateHelper
{

	/** close current session on setSession */
	public final static int ACTION_CLOSE = 1;
	
	/** disconnect current session on setSession */
	public final static int ACTION_DISCONNECT = 2;

	private static Log log = LogFactory.getLog(HibernateHelper.class);
	
	/**
	 * config url
	 */
	protected static URL configURL = null;
	
	/**
	 * Hibernate configuration object
	 */
	protected static Configuration configuration = null;

	/**
	 * Holds the current hibernate session, if one has been created.
	 */
	protected static ThreadLocal hibernateHolder = new ThreadLocal();

	/**
	 * Hibernate session factory
	 */
	protected static SessionFactory factory;

	private static boolean wasInitialised = false;

	/**
	 * initialise
	 */
	public static void init() throws Exception
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
	public static Session getSession() throws HibernateException
	{

		Session sess = (Session)hibernateHolder.get();
		if (sess == null && factory != null)
		{

			sess = factory.openSession();
			hibernateHolder.set(sess);
		}

		return sess;
	}
	
	/**
	 * close session for this Thread
	 */
	public static void closeSession() throws HibernateException
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
	public static void disconnectSession() throws HibernateException
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
	 * 		HibernateHelper.ACTION_CLOSE close current session
	 * 		HibernateHelper.ACTION_DISCONNECT disconnect current session
	 */
	public static void setSession(Session session, int actionForCurrentSession)
	{
		Session sess = (Session)hibernateHolder.get();
		if (sess != null)
		{
			if(actionForCurrentSession == ACTION_CLOSE)
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
			else if(actionForCurrentSession == ACTION_DISCONNECT)
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
	public static SessionFactory getSessionFactory()
	{
		return factory;
	}
	/**
	 * @param factory
	 */
	public static void setSessionFactory(SessionFactory factory)
	{
		HibernateHelper.factory = factory;
	}

	/**
	 * @return URL
	 */
	public static URL getConfigURL()
	{
		return configURL;
	}

	/**
	 * @param url
	 */
	public static void setConfigURL(URL url)
	{
		configURL = url;
	}

	/**
	 * @return
	 */
	public static Configuration getConfiguration()
	{
		return configuration;
	}

}
