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
 
package nl.openedge.util.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Keeps track of sessions
 * @author	Eelco Hillenius
 */
public class SessionListener
			    implements HttpSessionListener,
						   HttpSessionActivationListener {

	/* logger */
	private Log log = LogFactory.getLog(SessionListener.class);
	
	/* sessions */
	private List sessions = Collections.synchronizedList(new ArrayList());

	/**
	 * default constructor
	 */
	public SessionListener() {
		// nothing here
	}


    /**
     * Record the fact that a session has been created.
     * add session to internal store
     * @param event session event
     */
    public void sessionCreated(HttpSessionEvent event) {
		
		log.info(event.getSession().getId() + " created");
		//sessions.add(event.getSession());
    }


    /**
     * Record the fact that a session has been destroyed.
     * Remove session from internal store
     * @param event session event
     */
    public void sessionDestroyed(HttpSessionEvent event) {

		log.info(event.getSession().getId() + " destroyed");
		//sessions.remove(event.getSession());
    }
    
	/**
	 * Notification that the session is about to be passivated
	 * @param event session event
	 */
	public void sessionWillPassivate(HttpSessionEvent event) {
		
		log.info(event.getSession().getId() + " passivated");	
	}
	
	/**
	 * Notification that the session has just been activated
	 * @param event session event
	 */
	public void sessionDidActivate(HttpSessionEvent event) {
		
		log.info(event.getSession().getId() + " activated");
	}
    	
}

