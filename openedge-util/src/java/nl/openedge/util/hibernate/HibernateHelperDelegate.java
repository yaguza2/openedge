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

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.cfg.Configuration;

/**
 * Interface for implementing the behaviour of HibernateHelper.
 * @author Eelco Hillenius
 */
public interface HibernateHelperDelegate
{

	/**
	 * initialise.
	 * @throws ConfigException when an exception occurs during initialization
	 */
	void init() throws ConfigException;

	/**
	 * Get session for this Thread.
	 * 
	 * @return an appropriate Session object
	 * @throws HibernateException when an unexpected Hibernate exception occurs
	 */
	Session getSession() throws HibernateException;

	/**
	 * close session for this Thread.
	 * @throws HibernateException when an unexpected Hibernate exception occurs
	 */
	void closeSession() throws HibernateException;

	/**
	 * disconnect session and remove from threadlocal for this Thread.
	 * @throws HibernateException when an unexpected Hibernate exception occurs
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
	 * @param factory the session factory
	 */
	void setSessionFactory(SessionFactory factory);

	/**
	 * Get the configuration URL.
	 * @return URL the configuration url
	 */
	URL getConfigURL();

	/**
	 * Set the configuration URL.
	 * @param url the configuration URL
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
	 * If true, only one instance will be created of the interceptor for all sessions,
	 * if false, a new - and thus thread safe - instance will be created for session.
	 * 
	 * @param b If true, only one instance will be created of the interceptor
	 * for all sessions, if false, a new - and thus thread safe - instance will
	 * be created for session
	 */
	void setSingleInterceptor(boolean b);

}