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
package nl.openedge.access;

import java.io.IOException;
import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;

import javax.security.auth.AuthPermission;
import javax.security.auth.Subject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import nl.openedge.access.cache.Cache;
import nl.openedge.access.cache.HashMapCacheImpl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The AccessFilter can be used for automatic authentication of web applications.
 * It depends on a saved instance of <code>javax.security.auth.Subject</code>
 * stored under key '_authenticatedSubject' in the session. For non-protected
 * resource this may be null/ unsaved.
 * There are three ways of execution when this filter is called:
 * <ul>
 * <li>The user has sufficient rights for the request. The filter will pass 
 * execution to the next link in the chain of responsibilty.
 * <li>The user does not have sufficient rights and was not logged on (i.e. 
 * there was no subject saved in the session. A redirect to the configured login 
 * location is tried. This location is configured as init-parameter 'loginRedirect'
 * of this filter. For example:<pre>
 *   &lt;filter&gt;
 * 	&lt;filter-name&gt;OEAccess&lt;/filter-name&gt;
 * 	&lt;filter-class&gt;nl.openedge.access.AccessFilter&lt;/filter-class&gt;
 * 	&lt;init-param&gt;
 *	    &lt;param-name&gt;
 *	       loginRedirect
 *	    &lt;/param-name&gt;
 *	    &lt;param-value&gt;
 *	      /login.m
 *	    &lt;/param-value&gt;
 *	&lt;/init-param&gt;
 *	&lt;/filter&gt;
 * </pre&gt;where /login.m is the login command. Before the login location is tried,
 * the current request is saved as a request attribute so that it can be used
 * by the login logic to redirect to the original request when a login succeeded.
 * </ul&gt;The user does not have sufficient rights and was logged on. In this case
 * the user - though authenticated as a valid user - does not have permission for
 * this resource. The error 'FORBIDDEN' (403) is sent to the client and 
 * no further processing is done.
 * 
 * @author Eelco Hillenius
 */
public final class AccessFilter implements Filter
{

	/** key for storage of subject in session */
	public final static String AUTHENTICATED_SUBJECT_KEY = "_authenticatedSubject";

	/** last request before logon redirect will be saved as a request parameter */
	public final static String LAST_REQUEST_KEY = "_lastRequest";

	/** save cached permissions in session */
	public final static String SESSION_CACHE_KEY = "_jaasCache";

	/** if authentication failed or was not done yet, redirect to this url */
	protected static String loginRedirect;

	/** OpenEdge Access factory */
	protected static AccessController accessFactory = null;

	/** keep a reference to the config for later use */
	protected FilterConfig config = null;

	/** log */
	protected Log log = LogFactory.getLog(getClass());

	/**
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig config) throws ServletException
	{

		this.config = config;
		loginRedirect = config.getInitParameter("loginRedirect");
		if (loginRedirect == null)
			loginRedirect = "/";
		else if (loginRedirect.charAt(0) != '/')
			loginRedirect = "/" + loginRedirect;
	}

	/**
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
		throws IOException, ServletException
	{

		HttpServletRequest request = (HttpServletRequest)req;
		HttpServletResponse response = (HttpServletResponse)res;
		HttpSession session = request.getSession();
		Object cache = session.getAttribute( SESSION_CACHE_KEY);
		if( cache == null)
		{
		    // nieuwe cache aanmaken wanneer deze nog niet bestaat 
		    // (eerste request van een sessie)
		    cache = new HashMapCacheImpl();
		    session.setAttribute( SESSION_CACHE_KEY, cache);
		}
		AccessHelper.setCache( (Cache) cache);

		Subject subject = (Subject)session.getAttribute(AUTHENTICATED_SUBJECT_KEY);
		boolean needsAuthentication = false;
		String uri = ((HttpServletRequest)req).getRequestURI();
		// strip contextpath
		uri = uri.substring(request.getContextPath().length());
		// strip sessionId
		int sx = uri.indexOf(';');
		if(sx > -1)
		{
			uri = uri.substring(0, sx);
		}
		// compact
		while(uri.charAt(0) == '/')
		{
			uri = uri.substring(1);
		}
		// now, add 1 slash
		uri = '/' + uri;

		UriAction action = new UriAction(uri);
		try
		{
			Subject.doAsPrivileged(subject, action, null);
			
			/*
			Invoke the action via a doAsPrivileged. This allows the
			action to be executed as the client subject, and it also 
			runs that code as privileged. This means that any permission 
			checking that happens beyond this point applies only to 
			the code being run as the client.
			*/

			// if we get here, the user was authorised
			chain.doFilter(req, res);
			return;
		}
		catch (SecurityException se)
		{
			// Subject does not have permission
			log.info("for subject '" + subject + "', uri '" + uri 
						+ "': " + se.getMessage());
			if (subject == null)
				needsAuthentication = true;
		}

		// if this is a proctected request, try to retrieve subject from session
		if (needsAuthentication)
		{

			if (subject == null)
			{
				// save this request
				StringBuffer lq = request.getRequestURL();
				if (request.getQueryString() != null)
				{
					lq = lq.append("?").append(request.getQueryString());
				}

				session.setAttribute(LAST_REQUEST_KEY, lq.toString());
				// redirect to login address

				RequestDispatcher dispatcher = 
					config.getServletContext().getRequestDispatcher(loginRedirect);
				dispatcher.forward(request, response);

			}
			else
			{
				// the subject was not authorised; send error
				((HttpServletResponse)res).sendError(
					HttpServletResponse.SC_FORBIDDEN,
					"you do not have sufficient rights for this resource");
			}
		}
		else
		{
			// the subject was not authorised; send error
			((HttpServletResponse)res).sendError(
				HttpServletResponse.SC_FORBIDDEN,
				"you do not have sufficient rights for this resource");
		}
	}

	/**
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy()
	{

	}

	/** action for checking permissions for a specific subject */
	class UriAction implements PrivilegedAction
	{

		// uri to check on
		private String uri;

		/** construct with uri */
		public UriAction(String uri)
		{
			this.uri = uri;
		}

		/** run check */
		public Object run()
		{
			Permission p = new AuthPermission(uri);
			AccessController.checkPermission(p);
			return null;
		}
	}

}
