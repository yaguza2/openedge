/*
 * $Header$
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

import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;

/**
 * @author Eelco Hillenius
 */
public class AbstractHibernateModule {
	
	/**
	 * construct
	 * 
	 * IMPORTANT: if we are using the Hibernate filter (i.e. this is a webapp)
	 * 	the Hibernate factory was allready initialised. If we are NOT using
	 * 	the Hibernate filter (which we know if the session factory was not
	 * 	build yet), we initiliase the helper in this constructor	 
	 */
	public AbstractHibernateModule() {
		
		if(HibernateHelper.getSessionFactory() == null) {
			// we are not using the filter... initialise the helper
			try {
				HibernateHelper.init();
			} catch(Exception e) {
				// as we do not want to depend on the OpenEdge Modules framework here
				// it's best to stop the modules initialisation - if used - by throwing
				// a runtime exception
				throw new RuntimeException(e);
			}
		}
	}
	
	/**
	 * get current open session
	 * @return Session
	 * @throws Exception
	 */
	protected Session getCurrentSession() throws Exception {
		return borrowSession(false);
	}
	
	/**
	 * get session
	 * @param newSession if true, the current session from filter
	 * will be disconnected and a new Session will be started
	 * If newSession, the caller MUST call returnSession!
	 * @throws NamingException
	 */
	protected Session borrowSession(boolean newSession) throws Exception {
		
		if(newSession) {
			
			SessionFactory sessionFactory = HibernateHelper.getSessionFactory();
			Session currentSession = HibernateFilter.getSession();
			currentSession.disconnect();
			return sessionFactory.openSession();
		
		} else {		
			return HibernateFilter.getSession();
		}
	}
	
	/**
	 * close session and re-open the global (filter-) session
	 * must be called when a session was borrowed using newSession
	 * @param borrowedSession
	 * @return
	 * @throws Exception
	 */
	protected void returnSession(Session borrowedSession) throws Exception {

		borrowedSession.close();
		Session currentSession = HibernateHelper.getSession();
		currentSession.reconnect();
	}

}
