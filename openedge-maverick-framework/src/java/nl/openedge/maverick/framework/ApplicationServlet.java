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


import java.sql.Timestamp;
import java.util.Date;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import nl.openedge.access.AccessHelper;
import nl.openedge.maverick.framework.converters.BooleanConverter;
import nl.openedge.maverick.framework.converters.ByteConverter;
import nl.openedge.maverick.framework.converters.ByteLocaleConverter;
import nl.openedge.maverick.framework.converters.CharacterConverter;
import nl.openedge.maverick.framework.converters.DoubleConverter;
import nl.openedge.maverick.framework.converters.DoubleLocaleConverter;
import nl.openedge.maverick.framework.converters.FallbackDateConverter;
import nl.openedge.maverick.framework.converters.FloatConverter;
import nl.openedge.maverick.framework.converters.FloatLocaleConverter;
import nl.openedge.maverick.framework.converters.IntegerConverter;
import nl.openedge.maverick.framework.converters.LongConverter;
import nl.openedge.maverick.framework.converters.LongLocaleConverter;
import nl.openedge.maverick.framework.converters.ShortConverter;
import nl.openedge.maverick.framework.converters.ShortLocaleConverter;
import nl.openedge.modules.JDOMConfigurator;
import nl.openedge.util.hibernate.HibernateHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This servlet should startup first. Does initialisation
 * event handlers
 * @author Eelco Hillenius
 */
public class ApplicationServlet extends HttpServlet 
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
			initConverters();
			
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
	protected void initConverters()
	{
		// get the converter registry
		ConverterRegistry reg = ConverterRegistry.getInstance();
		
		// now, for all (basic) types, register a default converter and
		// - if available - a localized converter
		// NOTE: the localized versions precede the non-localized 
		
		reg.register(new BooleanConverter(), Boolean.TYPE);
		reg.register(new BooleanConverter(), Boolean.class);
		
		reg.register(new ByteConverter(), Byte.TYPE);
		reg.register(new ByteConverter(), Byte.class);
		reg.register(new ByteLocaleConverter(), Byte.TYPE);
		reg.register(new ByteLocaleConverter(), Byte.class);
		
		reg.register(new CharacterConverter(), Character.TYPE);
		reg.register(new CharacterConverter(), Character.class);
		
		reg.register(new DoubleConverter(), Double.TYPE);
		reg.register(new DoubleConverter(), Double.class);
		reg.register(new DoubleLocaleConverter(), Double.TYPE);
		reg.register(new DoubleLocaleConverter(), Double.class);

		reg.register(new FloatConverter(), Float.TYPE);
		reg.register(new FloatConverter(), Float.class);
		reg.register(new FloatLocaleConverter(), Float.TYPE);
		reg.register(new FloatLocaleConverter(), Float.class);		
		
		reg.register(new IntegerConverter(), Integer.TYPE);
		reg.register(new IntegerConverter(), Integer.class);
			
		reg.register(new LongConverter(), Long.TYPE);
		reg.register(new LongConverter(), Long.class);
		reg.register(new LongLocaleConverter(), Long.TYPE);
		reg.register(new LongLocaleConverter(), Long.class);
		
		reg.register(new ShortConverter(), Short.TYPE);
		reg.register(new ShortConverter(), Short.class);
		reg.register(new ShortLocaleConverter(), Short.TYPE);
		reg.register(new ShortLocaleConverter(), Short.class);
		
		reg.register(new FallbackDateConverter(), Date.class);
		reg.register(new FallbackDateConverter(), java.sql.Date.class);
		reg.register(new FallbackDateConverter(), Timestamp.class);

	}
	
	/**
	 * initialise OpenEdge Access with servlet context
	 * @param config
	 * @throws Exception
	 */
	protected void initOEAccess(ServletConfig config) throws Exception
	{
		AccessHelper.reload(config.getServletContext());
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

}
