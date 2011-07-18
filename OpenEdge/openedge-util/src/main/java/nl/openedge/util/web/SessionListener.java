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

package nl.openedge.util.web;

import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Keeps track of sessions.
 * 
 * @author Eelco Hillenius
 */
public class SessionListener implements HttpSessionListener, HttpSessionActivationListener
{
	/** sessions. */
	private static Vector<HttpSession> sessions = new Vector<HttpSession>();

	/** logger. */
	private Logger log = LoggerFactory.getLogger(SessionListener.class);

	/**
	 * Constructor.
	 */
	public SessionListener()
	{
		// nothing here
	}

	/**
	 * Record the fact that a session has been created. add session to internal store.
	 * 
	 * @param event
	 *            session event
	 */
	@Override
	public void sessionCreated(HttpSessionEvent event)
	{
		log.info(event.getSession().getId() + " created");
		synchronized (sessions)
		{
			sessions.add(event.getSession());
		}
	}

	/**
	 * Record the fact that a session has been destroyed. Remove session from internal
	 * store.
	 * 
	 * @param event
	 *            session event
	 */
	@Override
	public void sessionDestroyed(HttpSessionEvent event)
	{
		log.info(event.getSession().getId() + " destroyed");
		synchronized (sessions)
		{
			sessions.remove(event.getSession());
		}
	}

	/**
	 * Notification that the session is about to be passivated.
	 * 
	 * @param event
	 *            session event
	 */
	@Override
	public void sessionWillPassivate(HttpSessionEvent event)
	{

		log.info(event.getSession().getId() + " passivated");
		synchronized (sessions)
		{
			sessions.remove(event.getSession());
		}
	}

	/**
	 * Notification that the session has just been activated.
	 * 
	 * @param event
	 *            session event
	 */
	@Override
	public void sessionDidActivate(HttpSessionEvent event)
	{
		log.info(event.getSession().getId() + " activated");
		synchronized (sessions)
		{
			sessions.add(event.getSession());
		}
	}

	/**
	 * get known sessions (for this server).
	 * 
	 * @return List sessions known to this listener
	 */
	@SuppressWarnings("unchecked")
	public static List<HttpSession> getSessions()
	{
		synchronized (sessions)
		{
			return (List<HttpSession>) sessions.clone();
		}
	}

}
