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
 * @author Eelco Hillenius
 */
public interface HibernateHelperDelegate {
	
	/**
	 * initialise
	 */
	public void init() throws Exception;

	/**
	 * Get session for this Thread
	 *
	 * @return an appropriate Session object
	 */
	public Session getSession() throws HibernateException;
	
	/**
	 * close session for this Thread
	 */
	public void closeSession() throws HibernateException;
	
	/**
	 * disconnect session and remove from threadlocal for this Thread
	 */
	public void disconnectSession() throws HibernateException;
	
	/**
	 * set current session
	 * @param session hibernate session
	 * @param actionForCurrentSession one of the constants 
	 * 		HibernateHelperThreadLocaleImpl.ACTION_CLOSE close current session
	 * 		HibernateHelperThreadLocaleImpl.ACTION_DISCONNECT disconnect current session
	 */
	public void setSession(Session session, int actionForCurrentSession);

	/**
	 * @return the hibernate session factory
	 */
	public SessionFactory getSessionFactory();
	
	/**
	 * @param factory
	 */
	public void setSessionFactory(SessionFactory factory);

	/**
	 * @return URL
	 */
	public URL getConfigURL();

	/**
	 * @param url
	 */
	public void setConfigURL(URL url);

	/**
	 * @return Configuration
	 */
	public Configuration getConfiguration();
	
	/**
	 * get factory level interceptor class name
	 * @return String factory level interceptor class name
	 */
	public String getInterceptorClass();

	/**
	 * set factory level interceptor class name
	 * @param interceptor factory level interceptor class name
	 */
	public void setInterceptorClass(String className);

	/**
	 * If true, only one instance will be created of the interceptor for all
	 * sessions, if false, a new - and thus thread safe - instance will be created
	 * for session.
	 * @return boolean
	 */
	public boolean isSingleInterceptor();

	/**
	 * If true, only one instance will be created of the interceptor for all
	 * sessions, if false, a new - and thus thread safe - instance will be created
	 * for session.
	 * @param b
	 */
	public void setSingleInterceptor(boolean b);

}
