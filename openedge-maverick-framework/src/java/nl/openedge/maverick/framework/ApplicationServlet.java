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
package nl.openedge.maverick.framework;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.openedge.access.AccessFactory;
import nl.openedge.maverick.framework.converters.BooleanConverter;
import nl.openedge.maverick.framework.converters.ByteConverter;
import nl.openedge.maverick.framework.converters.CharacterConverter;
import nl.openedge.maverick.framework.converters.DoubleConverter;
import nl.openedge.maverick.framework.converters.FloatConverter;
import nl.openedge.maverick.framework.converters.IntegerConverter;
import nl.openedge.maverick.framework.converters.LongConverter;
import nl.openedge.maverick.framework.converters.ShortConverter;
import nl.openedge.modules.JDOMConfigurator;
import nl.openedge.util.DateConverter;
import nl.openedge.util.hibernate.HibernateHelper;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.Template;
import org.apache.velocity.app.event.EventCartridge;
import org.apache.velocity.app.event.ReferenceInsertionEventHandler;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.view.servlet.VelocityViewServlet;

/**
 * This servlet should startup first. Does initialisation and 
 * overrides the VelocityViewServlet to be able to hook 
 * event handlers
 * @author Eelco Hillenius
 */
public class ApplicationServlet extends VelocityViewServlet 
{

	/** logger */
	private static Log log = LogFactory.getLog(ApplicationServlet.class);

	/**
	 * initialise app
	 * @see javax.servlet.Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException 
	{

		// init superclass
		super.init(config);

		String realpath = config.getServletContext().getRealPath("/");

		try 
		{
			log.info("initialise");

			// init OpenEdge components
			initOEModules(config);

			// init OpenEdge access
			initOEAccess(config);

			// init beanutils for framework
			initBeanUtils();
			
			// init Hibernate helper
			HibernateHelper.init();

			log.info("initialised");

		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			throw new ServletException(e);
		}

	}
	
	/**
	 * Register all converters with empty constructor. This way, we achieve 
	 * that exceptions will be thrown for values that cannot be converted
	 * instead of having default values
	 */
	protected void initBeanUtils()
	{
		ConvertUtils.register(new BooleanConverter(), Boolean.TYPE);
		ConvertUtils.register(new BooleanConverter(), Boolean.class);
		ConvertUtils.register(new ByteConverter(), Byte.TYPE);
		ConvertUtils.register(new ByteConverter(), Byte.class);
		ConvertUtils.register(new CharacterConverter(), Character.TYPE);
		ConvertUtils.register(new CharacterConverter(), Character.class);
		ConvertUtils.register(new DoubleConverter(), Double.TYPE);
		ConvertUtils.register(new DoubleConverter(), Double.class);
		ConvertUtils.register(new FloatConverter(), Float.TYPE);
		ConvertUtils.register(new FloatConverter(), Float.class);
		ConvertUtils.register(new IntegerConverter(), Integer.TYPE);
		ConvertUtils.register(new IntegerConverter(), Integer.class);	
		ConvertUtils.register(new LongConverter(), Long.TYPE);
		ConvertUtils.register(new LongConverter(), Long.class);
		ConvertUtils.register(new ShortConverter(), Short.TYPE);
		ConvertUtils.register(new ShortConverter(), Short.class);
		ConvertUtils.register(new DateConverter(), Date.class);	
	}
	
	/**
	 * initialise OpenEdge Access with servlet context
	 * @param config
	 * @throws Exception
	 */
	protected void initOEAccess(ServletConfig config) throws Exception
	{

		AccessFactory.reload(config.getServletContext());
	}

	/**
	 * initialise OpenEdge Access with servlet context
	 * @param config
	 * @throws Exception
	 */
	protected void initOEModules(ServletConfig config) throws Exception
	{

		new JDOMConfigurator(config.getServletContext());
	}
	
	/**
	 *  Handles with both GET and POST requests
	 *
	 *  @param request  HttpServletRequest object containing client request
	 *  @param response HttpServletResponse object for the response
	 */
	protected void doRequest(HttpServletRequest request, 
							 HttpServletResponse response)
		 throws ServletException, IOException
	{
		try
		{
			// first, get a context
			Context context = createContext(request, response);
            
			// set the content type 
			setContentType(request, response);

			// get the template
			Template template = handleRequest(request, response, context);
			
			Object model = request.getAttribute("model");
			if(model != null) 
			{
				
				if(model instanceof AbstractForm) 
				{
					EventCartridge ec = new EventCartridge();
					ReferenceInsertionEventHandler evtHandler = 
						new RIEventHandler(
						(AbstractForm)model, context);
					ec.addEventHandler(evtHandler);
					ec.attachToContext(context);
				}
			}

			// bail if we can't find the template
			if (template == null)
			{
				return;
			}

			// merge the template and context
			mergeTemplate(template, context, response);

			// call cleanup routine to let a derived class do some cleanup
			requestCleanup(request, response, context);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			// call the error handler to let the derived class
			// do something useful with this failure.
			error(request, response, e);
		}
	}


	/**
	 * shutdown
	 */
	public void destroy() 
	{
		super.destroy();
	}

}
