/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ================================================================================
 * Copyright (c) 2004, Open Edge B.V.,  All Rights Reserved.
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

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Filter which manages a ThreadLocal hibernate session. Obtain the session by calling
 * HibernateFilter.getSession().
 * 
 * @author Jeff Schnitzer, Eelco Hillenius
 */
public final class HibernateFilter extends HibernateHelperThreadLocaleImpl implements Filter
{
	/** log. */
	private Logger log = LoggerFactory.getLogger(HibernateFilter.class);

	/** whether this filter 'works' or not. */
	private boolean active = false;

	/**
	 * Initialise filter. If an initparameter 'config' exists use the value to configure
	 * the HibernateHelperThreadLocaleImpl.
	 * 
	 * @param filterConfig
	 *            the filter config object
	 * @throws ServletException
	 *             when a servlet exception occurs
	 */
	@Override
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
			try
			{
				super.init();
			}
			catch (ConfigException e)
			{
				log.error(e.getMessage(), e);
				throw new ServletException(e);
			}
		}
	}

	/**
	 * Execute filter. If active == true, this filter tries to open a session, execute the
	 * next filters/ servlets and finally (at the end of the request execution) tries to
	 * close the session again.
	 * 
	 * @param request
	 *            http request
	 * @param response
	 *            http response
	 * @param chain
	 *            filter chain
	 * @throws IOException
	 *             when an io related exception occurs
	 * @throws ServletException
	 *             when a servlet exception occurs
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
	 *      javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException
	{
		if (active)
		{
			Session session = (Session) getHibernateHolder().get();
			if (session != null)
			{
				log.error("A session is already associated with this thread!  "
					+ "Someone must have called getSession() outside of the context "
					+ "of a servlet request; closing session");
				try
				{
					session.close();
				}
				catch (HibernateException e)
				{
					log.error(e.getMessage(), e);
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

				// log.info(Thread.currentThread() + ": closing " + sess);
				if (sess != null)
				{

					getHibernateHolder().set(null);

					try
					{
						sess.close();
					}
					catch (HibernateException ex)
					{
						log.error(ex.getMessage(), ex);
						// throw new ServletException(ex);
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
	@Override
	public void destroy()
	{

		// Nothing necessary
	}
}
