/*
 * $Id: ValidationTest.java,v 1.1 2004-04-01 09:20:35 eelco12 Exp $
 * $Revision: 1.1 $
 * $Date: 2004-04-01 09:20:35 $
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
 
package nl.openedge.baritus.test;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;

import nl.openedge.baritus.FormBeanCtrlBase;
import nl.openedge.baritus.test.mock.MockHttpServletRequest;
import nl.openedge.baritus.test.mock.MockHttpServletResponse;

import org.infohazard.maverick.flow.MaverickContext;

import com.mockobjects.servlet.MockHttpSession;
import com.mockobjects.servlet.MockRequestDispatcher;
import com.mockobjects.servlet.MockServletConfig;
import com.mockobjects.servlet.MockServletContext;

import junit.framework.TestCase;

/**
 * Testcase for population of form beans and interceptors
 * 
 * @author Eelco Hillenius
 */
public class ValidationTest extends TestCase
{
	
	private Locale dutch = new Locale("nl", "NL");

	private MockRequestDispatcher requestDispatcher = null;
	private MockServletContext servletContext = null;
	private MockServletConfig servletConfig = null;
	private MockHttpSession session = null;
	private MockHttpServletResponse response = null;		
	private MockHttpServletRequest request = null;

	/**
	 * construct
	 * @param name
	 */
	public ValidationTest(String name)
	{
		super(name);
	}
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		this.requestDispatcher = new MockRequestDispatcher();
		this.servletContext = new MockServletContext();
		this.servletContext.setupGetRequestDispatcher(requestDispatcher);
		this.servletConfig = new MockServletConfig();
		this.servletConfig.setServletContext(servletContext);
		this.session = new MockHttpSession();
		this.session.setupGetAttribute(
			FormBeanCtrlBase.SESSION_KEY_CURRENT_LOCALE, dutch);
			
		this.session.setupServletContext(servletContext);
		this.response = new MockHttpServletResponse();
		this.request = new MockHttpServletRequest();
		this.request.setupGetAttribute("__formBeanContext");
		this.request.setSession(session);
		this.request.setupGetRequestDispatcher(requestDispatcher);
	}
	
	public void testValidFieldValidation()
	{
		TestCtrl ctrl = new TestCtrl();
		Map requestParams = new HashMap();
		requestParams.put("toValidate1", "validValue");
		request.setupGetParameterMap(requestParams);
		MaverickContext mockMavCtx = new MaverickContext(
			null, request, response);
		try
		{
			ctrl.init(null);
			ctrl.go(mockMavCtx);
			TestBean bean = ctrl.getTestBean();
			assertEquals(FormBeanCtrlBase.SUCCESS, ctrl.getView());
		}
		catch (ServletException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}	
	}

	public void testNonValidFieldValidation()
	{
		TestCtrl ctrl = new TestCtrl();
		Map requestParams = new HashMap();
		requestParams.put("toValidate1", "nonValidValue");
		request.setupGetParameterMap(requestParams);
		MaverickContext mockMavCtx = new MaverickContext(
			null, request, response);
		try
		{
			ctrl.init(null);
			ctrl.go(mockMavCtx);
			TestBean bean = ctrl.getTestBean();
			assertEquals(FormBeanCtrlBase.ERROR, ctrl.getView());
		}
		catch (ServletException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}	
	}
	
	public void testIndexedFieldValidation1()
	{
		TestCtrl ctrl = new TestCtrl();
		Map requestParams = new HashMap();
		requestParams.put("toValidate2[0]", "validValue");
		requestParams.put("toValidate2[1]", "nonValidValue");
		request.setupGetParameterMap(requestParams);
		MaverickContext mockMavCtx = new MaverickContext(
			null, request, response);
		try
		{
			ctrl.init(null);
			ctrl.go(mockMavCtx);
			TestBean bean = ctrl.getTestBean();
			assertEquals(FormBeanCtrlBase.ERROR, ctrl.getView());
		}
		catch (ServletException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}	
	}
	
	public void testIndexedFieldValidation2()
	{
		TestCtrl ctrl = new TestCtrl();
		Map requestParams = new HashMap();
		requestParams.put("toValidate3[0]", "nonValidValue");
		request.setupGetParameterMap(requestParams);
		MaverickContext mockMavCtx = new MaverickContext(
			null, request, response);
		try
		{
			ctrl.init(null);
			ctrl.go(mockMavCtx);
			TestBean bean = ctrl.getTestBean();
			assertEquals(FormBeanCtrlBase.ERROR, ctrl.getView());
		}
		catch (ServletException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}	
	}
	
	public void testIndexedFieldValidation3()
	{
		TestCtrl ctrl = new TestCtrl();
		Map requestParams = new HashMap();
		requestParams.put("toValidate3[0]", "validValue");
		requestParams.put("toValidate3[1]", "nonValidValue"); // should not be checked
		request.setupGetParameterMap(requestParams);
		MaverickContext mockMavCtx = new MaverickContext(
			null, request, response);
		try
		{
			ctrl.init(null);
			ctrl.go(mockMavCtx);
			TestBean bean = ctrl.getTestBean();
			assertEquals(FormBeanCtrlBase.SUCCESS, ctrl.getView());
		}
		catch (ServletException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}	
	}

}
