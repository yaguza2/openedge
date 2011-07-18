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
package nl.openedge.util.baritus;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Locale;

import junit.framework.TestCase;
import nl.openedge.baritus.ConverterRegistry;
import nl.openedge.baritus.FormBeanCtrl;
import nl.openedge.baritus.FormBeanCtrlBase;
import nl.openedge.baritus.converters.DateLocaleConverter;
import nl.openedge.util.baritus.converters.FallbackDateConverter;
import nl.openedge.util.mock.MockHttpServletRequest;
import nl.openedge.util.mock.MockHttpServletResponse;

import com.mockobjects.servlet.MockHttpSession;
import com.mockobjects.servlet.MockRequestDispatcher;
import com.mockobjects.servlet.MockServletConfig;
import com.mockobjects.servlet.MockServletContext;

/**
 * Base class for testcases that are used to test controlls, validators etc. Note that allthough
 * this class depends on Mock Objects, its actual usage differs from the standard mock objects way.
 * Instead of the white box approach of mock objects (with recording and playback), you can use
 * these mock objects in a black box fashion, ie use them as if being in a real web environment.
 * 
 * @author Eelco Hillenius
 */
public abstract class BaritusControlTest extends TestCase
{
	/**
	 * Fixed locale (dutch locale, but you can overwrite this in setUpTestCase).
	 */
	protected Locale fixedLocale = new Locale("nl", "NL");

	/**
	 * request dispatcher.
	 */
	protected MockRequestDispatcher requestDispatcher = null;

	/**
	 * mock servlet context.
	 */
	protected MockServletContext servletContext = null;

	/**
	 * mock servlet config.
	 */
	protected MockServletConfig servletConfig = null;

	/**
	 * mock http sessie.
	 */
	protected MockHttpSession session = null;

	/**
	 * mock servlet response.
	 */
	protected MockHttpServletResponse response = null;

	/**
	 * mock servlet request.
	 */
	protected MockHttpServletRequest request = null;

	/**
	 * Construct.
	 */
	public BaritusControlTest()
	{
		super();
	}

	/**
	 * Construct with naam.
	 * 
	 * @param name
	 *            name of test
	 */
	public BaritusControlTest(final String name)
	{
		super(name);
	}

	/**
	 * Create fixture; set up mockobjects.
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected final void setUp() throws Exception
	{
		this.requestDispatcher = new MockRequestDispatcher();
		this.servletContext = new MockServletContext();
		this.servletContext.setupGetRequestDispatcher(requestDispatcher);
		this.servletConfig = new MockServletConfig();
		this.servletConfig.setServletContext(servletContext);
		this.session = new MockHttpSession();
		this.session.setupGetAttribute(FormBeanCtrlBase.SESSION_KEY_CURRENT_LOCALE, fixedLocale);
		this.session.setupServletContext(servletContext);
		this.response = new MockHttpServletResponse();
		this.request = new MockHttpServletRequest();
		this.request.setupGetAttribute("__formBeanContext");
		this.request.setSession(session);
		this.request.setupGetRequestDispatcher(requestDispatcher);
		setUpTestCase();
	}

	/**
	 * Sets up the fixture; use instead of setUp(). This method is called after the finalized setUp
	 * method is called.
	 */
	protected void setUpTestCase()
	{
		// noop
	}

	/**
	 * Breakdown fixture; remove references mockobjects.
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected final void breakDown() throws Exception
	{
		this.requestDispatcher = null;
		this.servletContext = null;
		this.servletConfig = null;
		this.session = null;
		this.response = null;
		this.request = null;
		breakDownTestCase();
	}

	/**
	 * Breaks down the fixture; use instead of breakDown(). This method is called after the
	 * finalized breakDown method is called.
	 */
	protected void breakDownTestCase()
	{
		// noop
	}

	/**
	 * Register converters.
	 */
	protected void initConverters()
	{
		// get the converter registry
		ConverterRegistry reg = ConverterRegistry.getInstance();
		reg.deregisterByConverterClass(DateLocaleConverter.class);
		reg.register(new FallbackDateConverter(), Date.class);
		reg.register(new FallbackDateConverter(), java.sql.Date.class);
		reg.register(new FallbackDateConverter(), Timestamp.class);
	}

}
