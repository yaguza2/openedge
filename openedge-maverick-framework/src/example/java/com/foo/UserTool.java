/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Copyright (c) 2003, Promedico ICT B.V.
 * All rights reserved.
 */
package com.foo;

import java.security.Principal;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import nl.openedge.access.AccessFilter;
import nl.openedge.access.UserPrincipal;
import nl.openedge.util.web.SessionListener;

/**
 * Velocity tool for user related things
 * 
 * @author Eelco Hillenius
 */
public class UserTool
{


	/**
	 * get the current user
	 * @param request
	 * @return UserPrincipal the user, null if none found with this request/ session
	 */
	public static UserPrincipal getUser(HttpServletRequest request)
	{
		return getUser(request.getSession());
	}

	/**
	 * get the user for a session
	 * @param session
	 * @return UserPrincipal the user, null if none found with this request/ session
	 */
	public static UserPrincipal getUser(HttpSession session)
	{

		UserPrincipal user = null;
		Subject subject = (Subject)session.getAttribute(
			AccessFilter.AUTHENTICATED_SUBJECT_KEY);
		if (subject != null)
		{
			Set p = subject.getPrincipals(UserPrincipal.class);
			if (!p.isEmpty())
			{
				user = (UserPrincipal)p.iterator().next();
			}
		}
		return user;
	}

	/**
	 * get the current subject
	 * @param request
	 * @return Subject the subject, null if none found with this request/ session
	 */
	public static Subject getSubject(HttpServletRequest request)
	{
		return getSubject(request.getSession());
	}

	/**
	 * get the subject for a session
	 * @param session
	 * @return Subject the subject, null if none found with this request/ session
	 */
	public static Subject getSubject(HttpSession session)
	{
		return (Subject)session.getAttribute(
			AccessFilter.AUTHENTICATED_SUBJECT_KEY);
	}
	
	/**
	 * print debug info
	 * @param request http servlet request
	 * @return String debug info
	 */
	public static String debugSubject(HttpServletRequest request)
	{
		Subject subject = getSubject(request);
		if( subject == null)
		{
			return "";
		}

		StringBuffer s = new StringBuffer(
			"<table border=\"1\"><tr><th>Subject</th></tr>");

		String suffix = new String();
		Iterator principals = subject.getPrincipals().iterator();
		Iterator pubCreds = subject.getPublicCredentials().iterator();

		while (principals.hasNext()) {
			Principal p = (Principal)principals.next();
			s.append("<tr><td>&nbsp;&nbsp; principal: ")
			 .append(p)
			 .append("</td></tr>");
		}

		while (pubCreds.hasNext()) {
			Object o = pubCreds.next();
			s.append("<tr><td>&nbsp;&nbsp; pub cred: ")
			 .append(o)
			 .append("</td></tr>");
		}	
		
		s.append("</table>");
		return s.toString();	
	}
	
	/**
	 * get sessions known to this webapp
	 * @return List
	 */
	public static List getSessions()
	{
		return SessionListener.getSessions();
	}

	/**
	 * get number of sessions known to this webapp
	 * @return int
	 */
	public static int getNumberOfSessions()
	{
		return SessionListener.getSessions().size();
	}

}
