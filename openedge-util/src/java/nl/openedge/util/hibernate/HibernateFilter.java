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

import java.io.IOException;
import java.net.URL;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;

/**
 * Filter which manages a ThreadLocal hibernate session. Obtain the session by calling
 * HibernateFilter.getSession().
 * 
 * @author Jeff Schnitzer, Eelco Hillenius
 */
public final class HibernateFilter extends HibernateHelperThreadLocaleImpl implements Filter
{

	/** log. */
	private Log log = LogFactory.getLog(HibernateFilter.class);

	/** whether this filter 'works' or not. */
	private boolean active = false;

	/**
	 * Initialise filter. If an initparameter 'config' exists use the value to configure the
	 * HibernateHelperThreadLocaleImpl.
	 * 
	 * @param filterConfig
	 *            the filter config object
	 * @throws ServletException when a servlet exception occurs
	 */
	public void init(FilterConfig filterConfig) throws ServletException
	{
		HibernateHelperDelegate delegate = HibernateHelper.getDelegate();
		if (delegate instanceof HibernateHelperThreadLocaleImpl)
		{
			active = true;
		}
		else
		{
			log.warn("This filter only functions when used with "
					+ HibernateHelperThreadLocaleImpl.class.getName()
					+ " as the HibernateHelperDelegate for HibernateHelper");
		}

		if (active)
		{
			// call to super will read config and create hibernate factory
			String configStr = filterConfig.getInitParameter("config");
			if (configStr != null)
			{
				URL configUrl = HibernateFilter.class.getClassLoader().getResource(configStr);
				log.info("Using configfile " + configUrl.toString());
				super.setConfigURL(configUrl);
			}
			super.init();
		}
	}

	/**
	 * Execute filter. If active == true, this filter tries to open a session, execute the next
	 * filters/ servlets and finally (at the end of the request execution) tries to close the
	 * session again.
	 * 
	 * @param request
	 *            http request
	 * @param response
	 *            http response
	 * @param chain
	 *            filter chain
	 * @throws IOException when an io related exception occurs
	 * @throws ServletException when a servlet exception occurs
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
	 *      javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException
	{
		if (active)
		{
			Session session = (Session) getHibernateHolder().get();
			if (session != null)
			{
				log.warn("A session is already associated with this thread!  "
						+ "Someone must have called getSession() outside of the context "
						+ "of a servlet request; closing session");
				try
				{
					session.close();
				}
				catch (HibernateException e)
				{
					log.error(e);
					throw new ServletException(e);
				}
				getHibernateHolder().set(null);
			}
		}

		try
		{
			chain.doFilter(request, response);
		}
		finally
		{
			if (active)
			{
				Session sess = (Session) getHibernateHolder().get();

				//log.info(Thread.currentThread() + ": closing " + sess);
				if (sess != null)
				{

					getHibernateHolder().set(null);

					try
					{
						sess.close();
					}
					catch (HibernateException ex)
					{
						log.error(ex);
						//throw new ServletException(ex);
					}
				}
			}
		}
	}

	/**
	 * Destroy this filter.
	 * 
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy()
	{

		// Nothing necessary
	}
}