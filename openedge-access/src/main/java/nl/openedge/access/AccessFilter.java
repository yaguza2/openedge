/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package nl.openedge.access;

import java.io.IOException;
import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;

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
public class AccessFilter implements Filter {

	/** key for storage of subject in session */
	public final static String AUTHENTICATED_SUBJECT_KEY =
		"_authenticatedSubject";
		
	/** last request before logon redirect will be saved as a request parameter */
	public final static String LAST_REQUEST_KEY = "lastRequest";

	/** if authentication failed or was not done yet, redirect to this url */
	protected static String loginRedirect;
	
	/** OpenEdge Access factory */
	protected static AccessFactory accessFactory = null;

	/** keep a reference to the config for later use */
	protected FilterConfig config = null;

	/** log */
	protected Log log = LogFactory.getLog(getClass());

	/**
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig config) throws ServletException {
		
		this.config = config;
		loginRedirect = config.getInitParameter("loginRedirect");
		if(loginRedirect == null) 	loginRedirect = "/";
		else if(loginRedirect.charAt(0) != '/') 
				loginRedirect = "/" + loginRedirect;
	}

	/**
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(	ServletRequest req,
							ServletResponse res,
							FilterChain chain)
							throws IOException, ServletException {
			
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		HttpSession session = request.getSession();

		Subject subject = (Subject)session.getAttribute(
								AUTHENTICATED_SUBJECT_KEY);
		boolean needsAuthentication = false;
		String uri = ((HttpServletRequest)req).getRequestURI();
		// strip contextpath
		uri = uri.substring(request.getContextPath().length());
		
		UriAction action = new UriAction(uri);
		try {
			// Subject.doAs(subject, action) does NOT work... dunno why?
			// nevertheless, this does. 
			Subject.doAsPrivileged(subject, action, null);
			// if we get here, the user was authorised
			chain.doFilter(req, res);
			return;
		} catch (SecurityException se) {
			// Subject does not have permission
			log.info("for subject '" + subject + 
					"', uri '" + uri + "': " + se.getMessage());
			if(subject == null) needsAuthentication = true;
		}


		// if this is a proctected request, try to retrieve subject from session
		if(needsAuthentication) {
			
			if( subject == null) {
				// save this request
				StringBuffer lq = request.getRequestURL();
				if(request.getQueryString() != null) {
					lq = lq.append("?").append(request.getQueryString());
				}
				
				request.setAttribute(LAST_REQUEST_KEY, lq.toString());
				// redirect to login address
				
				RequestDispatcher dispatcher = config.getServletContext()
						.getRequestDispatcher(loginRedirect);
				dispatcher.forward(request, response);

			} else {				
				// the subject was not authorised; send error
				((HttpServletResponse)res).sendError(HttpServletResponse.SC_FORBIDDEN,
						"you do not have sufficient rights for this resource");	
			}
		} else {
			// the subject was not authorised; send error
			((HttpServletResponse)res).sendError(HttpServletResponse.SC_FORBIDDEN,
					"you do not have sufficient rights for this resource");				
		}
	}

	/**
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
	
	}
	
	/** action for checking permissions for a specific subject */
	class UriAction implements PrivilegedAction {
		
		// uri to check on
		private String uri;
		
		/** construct with uri */
		public UriAction(String uri) {
			this.uri = uri;
		}
		
		/** run check */
		public Object run() {
			Permission p = new NamedPermission(uri);
			AccessController.checkPermission(p);
			return null;
		}
	}

}