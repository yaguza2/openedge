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
 
package nl.openedge.maverick.framework.test;


import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;

import nl.openedge.maverick.framework.FormBeanContext;
import nl.openedge.maverick.framework.FormBeanCtrl;
import nl.openedge.maverick.framework.test.mock.MockHttpServletRequest;
import nl.openedge.maverick.framework.test.mock.MockHttpServletResponse;

import org.infohazard.maverick.flow.MaverickContext;

import com.mockobjects.servlet.MockHttpSession;
import com.mockobjects.servlet.MockRequestDispatcher;
import com.mockobjects.servlet.MockServletConfig;
import com.mockobjects.servlet.MockServletContext;

import junit.framework.TestCase;

/**
 * Testcase for population of form beans
 * 
 * @author Eelco Hillenius
 */
public class PopulationTest extends TestCase
{
	
	private Locale dutch = new Locale("nl", "NL");

	private MockRequestDispatcher requestDispatcher = null;
	private MockServletContext servletContext = null;
	private MockServletConfig servletConfig = null;
	private MockHttpSession httpSession = null;
	private MockHttpServletResponse response = null;		
	private MockHttpServletRequest request = null;

	/**
	 * construct
	 * @param name
	 */
	public PopulationTest(String name)
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
		this.httpSession = new MockHttpSession();
		this.httpSession.setupGetAttribute(
			FormBeanCtrl.SESSION_KEY_CURRENT_LOCALE, dutch);
			
		this.httpSession.setupServletContext(servletContext);
		this.response = new MockHttpServletResponse();
		this.request = new MockHttpServletRequest();
		this.request.setSession(httpSession);
		this.request.setupGetRequestDispatcher(requestDispatcher);
	}
	
	/**
	 * test default, succesful (and localized) population and 
	 * localized formatting of a form bean
	 */
	public void testDefaultPopulateAndFormat()
	{
		TestCtrl ctrl = new TestCtrl();
		
		Map requestParams = new HashMap();
		
		requestParams.put("testInteger1", "1"); // test simple string
		requestParams.put("testInteger2", new String[]{"2"}); // test string array
		
		requestParams.put("testLong1", "1"); // test simple string
		requestParams.put("testLong2", new String[]{"2"}); // test string array
		
		requestParams.put("testDouble1", "1,1"); // test simple string
		requestParams.put("testDouble2", new String[]{"1,2"}); // test string array
		
		requestParams.put("testDate1", "20-02-2004"); // test simple string
		requestParams.put("testDate2", new String[]{"21-03-2005"}); // test string array
		
		requestParams.put("testStringArray1", new String[] {"arrayelem0", "arrayelem1"});
		
		requestParams.put("testStringArray2[0]", "newval0");
		requestParams.put("testStringArray2[1]", "newval1");
		
		requestParams.put("testMap(key1)", "val1");
		requestParams.put("testMap(key2)", "val2");
		
		request.setupGetParameterMap(requestParams);

		MaverickContext mockMavCtx = new MaverickContext(
			null, request, response);
		
		try
		{
			// execute command method
			ctrl.go(mockMavCtx);
			
			// get populated bean
			TestBean bean = ctrl.getTestBean();
			
			assertEquals(FormBeanCtrl.SUCCESS, ctrl.getView());
			
			assertNotNull(bean.getTestInteger1());
			assertEquals(new Integer(1), bean.getTestInteger1());
			assertNotNull(bean.getTestInteger2());
			assertEquals(new Integer(2), bean.getTestInteger2());
			
			assertNotNull(bean.getTestLong1());
			assertEquals(new Long(1), bean.getTestLong1());
			assertNotNull(bean.getTestLong2());
			assertEquals(new Long(2), bean.getTestLong2());
			
			assertNotNull(bean.getTestDouble1());
			assertEquals(new Double(1.1), bean.getTestDouble1());
			assertNotNull(bean.getTestDouble2());
			assertEquals(new Double(1.2), bean.getTestDouble2());
			
			assertNotNull(bean.getTestDate1());
			assertNotNull(bean.getTestDate2());
			
			Calendar cal = Calendar.getInstance();
			Date date = bean.getTestDate1();
			cal.setTime(date);
			assertEquals(cal.get(Calendar.YEAR), 2004);
			assertEquals(cal.get(Calendar.MONTH), 1);
			assertEquals(cal.get(Calendar.DAY_OF_MONTH), 20);
			
			date = bean.getTestDate2();
			cal.setTime(date);
			assertEquals(cal.get(Calendar.YEAR), 2005);
			assertEquals(cal.get(Calendar.MONTH), 2);
			assertEquals(cal.get(Calendar.DAY_OF_MONTH), 21);
			
			String[] testStringArray1 = bean.getTestStringArray1();
			assertNotNull(testStringArray1);
			assertEquals(2, testStringArray1.length);
			assertEquals("arrayelem0", testStringArray1[0]);
			assertEquals("arrayelem1", testStringArray1[1]);
			
			String[] testStringArray2 = bean.getTestStringArray2();
			assertNotNull(testStringArray2);
			assertEquals(2, testStringArray2.length);
			assertEquals("newval0", testStringArray2[0]);
			assertEquals("newval1", testStringArray2[1]);
			
			Map map = bean.getTestMap();
			assertNotNull(map);
			assertEquals(2, map.size());
			assertEquals("val1", map.get("key1"));
			assertEquals("val2", map.get("key2"));
			
			FormBeanContext formBeanContext = ctrl.getFormBeanContext();
			assertEquals("dutch locale should be used for formatting a double property",
				"1,1", formBeanContext.displayProperty("testDouble1"));
			
		}
		catch (ServletException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
		
	}

}
