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
 * Filter which manages a ThreadLocal hibernate session.  Obtain the session
 * by calling HibernateFilter.getSession().
 *
 * @author Jeff Schnitzer, Eelco Hillenius
 */
public final class HibernateFilter extends HibernateHelper implements Filter
{

	private Log log = LogFactory.getLog(HibernateFilter.class);

	/**
	 * initialise
	 */
	public void init(FilterConfig filterConfig) throws ServletException
	{
		// call to super will read config and create hibernate factory
		try
		{
			super.init();
		}
		catch (Exception e)
		{
			throw new ServletException(e);
		}
	}

	/**
	 */
	public void doFilter(ServletRequest request, 
		ServletResponse response, FilterChain chain)
		throws IOException, ServletException
	{
		Session session = (Session)hibernateHolder.get();
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
				e.printStackTrace();
				throw new ServletException(e);
			}
			hibernateHolder.set(null);
		}

		try
		{
			chain.doFilter(request, response);
		}
		finally
		{
			Session sess = (Session)hibernateHolder.get();

			//log.info(Thread.currentThread() + ": closing " + sess);
			if (sess != null)
			{

				hibernateHolder.set(null);

				try
				{
					sess.close();
				}
				catch (HibernateException ex)
				{
					ex.printStackTrace();
					//throw new ServletException(ex);
				}
			}
		}
	}

	/**
	 */
	public void destroy()
	{

		// Nothing necessary
	}
}
