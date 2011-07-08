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

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.classic.Session;

/**
 * Interface for implementing the behaviour of HibernateHelper.
 * 
 * @author Eelco Hillenius
 */
public interface HibernateHelperDelegate
{

	/**
	 * initialise.
	 * 
	 * @throws ConfigException
	 *             when an exception occurs during initialization
	 */
	void init() throws ConfigException;

	/**
	 * Get session for this Thread.
	 * 
	 * @return an appropriate Session object
	 * @throws HibernateException
	 *             when an unexpected Hibernate exception occurs
	 */
	Session getSession() throws HibernateException;

	/**
	 * close session for this Thread.
	 * 
	 * @throws HibernateException
	 *             when an unexpected Hibernate exception occurs
	 */
	void closeSession() throws HibernateException;

	/**
	 * disconnect session and remove from threadlocal for this Thread.
	 * 
	 * @throws HibernateException
	 *             when an unexpected Hibernate exception occurs
	 */
	void disconnectSession() throws HibernateException;

	/**
	 * set current session.
	 * 
	 * @param session
	 *            hibernate session
	 * @param actionForCurrentSession
	 *            one of the constants HibernateHelperThreadLocaleImpl.ACTION_CLOSE close current
	 *            session HibernateHelperThreadLocaleImpl.ACTION_DISCONNECT disconnect current
	 *            session
	 */
	void setSession(Session session, int actionForCurrentSession);

	/**
	 * @return the hibernate session factory
	 */
	SessionFactory getSessionFactory();

	/**
	 * Set the session factory.
	 * 
	 * @param factory
	 *            the session factory
	 */
	void setSessionFactory(SessionFactory factory);

	/**
	 * Get the configuration URL.
	 * 
	 * @return URL the configuration url
	 */
	URL getConfigURL();

	/**
	 * Set the configuration URL.
	 * 
	 * @param url
	 *            the configuration URL
	 */
	void setConfigURL(URL url);

	/**
	 * @return Configuration
	 */
	Configuration getConfiguration();

	/**
	 * get factory level interceptor class name.
	 * 
	 * @return String factory level interceptor class name
	 */
	String getInterceptorClass();

	/**
	 * set factory level interceptor class name.
	 * 
	 * @param className
	 *            factory level interceptor class name
	 */
	void setInterceptorClass(String className);

	/**
	 * If true, only one instance will be created of the interceptor for all sessions, if false, a
	 * new - and thus thread safe - instance will be created for session.
	 * 
	 * @return boolean
	 */
	boolean isSingleInterceptor();

	/**
	 * If true, only one instance will be created of the interceptor for all sessions, if false, a
	 * new - and thus thread safe - instance will be created for session.
	 * 
	 * @param b
	 *            If true, only one instance will be created of the interceptor for all sessions, if
	 *            false, a new - and thus thread safe - instance will be created for session
	 */
	void setSingleInterceptor(boolean b);

}
