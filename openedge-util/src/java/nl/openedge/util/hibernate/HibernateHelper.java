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
public abstract class HibernateHelper {
	  
	private static Log log = LogFactory.getLog(HibernateHelper.class);
	    
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
        
        if(!wasInitialised)
        {       
			wasInitialised = true;
	        // Initialize hibernate
			// configure; load mappings
			Configuration ds = new Configuration().configure();
			// build a SessionFactory		
			factory = ds.buildSessionFactory();
        } 
     }
     
    
    /**
     * Get session for this Thread
     *
     * @return an appropriate Session object
     */
    public static Session getSession() throws HibernateException {
    	
		//log.info(Thread.currentThread() + ": get session");
        Session sess = (Session)hibernateHolder.get();
        if (sess == null && factory != null) {
        	
            sess = factory.openSession();
			//log.info(Thread.currentThread() + "create session");
            hibernateHolder.set(sess);
        } else {
			//log.info(Thread.currentThread() + ": got it");
        }
        
        return sess;
    }
    
    /**
     * @return the hibernate session factory
     */
    public static SessionFactory getSessionFactory() {
    	
        return factory;
    }
	/**
	 * @param factory
	 */
	public static void setSessionFactory(SessionFactory factory) {
		HibernateHelper.factory = factory;
	}

}
