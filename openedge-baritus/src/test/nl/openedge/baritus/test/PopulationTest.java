/*
 * $Id: PopulationTest.java,v 1.4 2004-04-04 18:24:07 eelco12 Exp $
 * $Revision: 1.4 $
 * $Date: 2004-04-04 18:24:07 $
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

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;

import nl.openedge.baritus.FormBeanContext;
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
 * Testcase for population of form beans and interceptors.
 * 
 * @author Eelco Hillenius
 * @author Sander Hofstee
 */
public class PopulationTest extends TestCase
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
	

	public void testIntegerPopulationAndBeforePerformInterceptor()
	{
		TestCtrl ctrl = new TestCtrl();
		Map requestParams = new HashMap();
		requestParams.put("testInteger1", "1"); // test simple string
		requestParams.put("testInteger2", new String[]{"2"}); // test string array
		request.setupGetParameterMap(requestParams);
		MaverickContext mockMavCtx = new MaverickContext(
			null, request, response);
		
		try
		{
			ctrl.init(null);
			ctrl.go(mockMavCtx);
			TestBean bean = ctrl.getTestBean();
			assertEquals(FormBeanCtrlBase.SUCCESS, ctrl.getView());
			assertNotNull(bean.getTestInteger1());
			assertEquals(new Integer(1), bean.getTestInteger1());
			assertNotNull(bean.getTestInteger2());
			assertEquals(new Integer(2), bean.getTestInteger2());
		}
		catch (ServletException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}	
	}
	

	public void testLongPopulation()
	{
		TestCtrl ctrl = new TestCtrl();
		Map requestParams = new HashMap();
		requestParams.put("testLong1", "1"); // test simple string
		requestParams.put("testLong2", new String[]{"2"}); // test string array
		request.setupGetParameterMap(requestParams);
		MaverickContext mockMavCtx = new MaverickContext(
			null, request, response);
		try
		{
			ctrl.init(null);
			ctrl.go(mockMavCtx);
			TestBean bean = ctrl.getTestBean();
			assertEquals(FormBeanCtrlBase.SUCCESS, ctrl.getView());
			assertNotNull(bean.getTestLong1());
			assertEquals(new Long(1), bean.getTestLong1());
			assertNotNull(bean.getTestLong2());
			assertEquals(new Long(2), bean.getTestLong2());
		}
		catch (ServletException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}	
	}
	

	public void testDoublePopulationAndLocalizedDisplayProperty()
	{
		TestCtrl ctrl = new TestCtrl();
		Map requestParams = new HashMap();
		requestParams.put("testDouble1", "1,1"); // test simple string
		requestParams.put("testDouble2", new String[]{"1,2"}); // test string array
		request.setupGetParameterMap(requestParams);
		MaverickContext mockMavCtx = new MaverickContext(
			null, request, response);
		try
		{
			ctrl.init(null);
			ctrl.go(mockMavCtx);
			TestBean bean = ctrl.getTestBean();
			assertEquals(FormBeanCtrlBase.SUCCESS, ctrl.getView());
			assertNotNull(bean.getTestDouble1());
			assertEquals(new Double(1.1), bean.getTestDouble1());
			assertNotNull(bean.getTestDouble2());
			assertEquals(new Double(1.2), bean.getTestDouble2());
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
	
	public void testDatePopulation()
	{
		TestCtrl ctrl = new TestCtrl();
		Map requestParams = new HashMap();
		requestParams.put("testDate1", "20-02-2004"); // test simple string
		requestParams.put("testDate2", new String[]{"21-03-2005"}); // test string array
		request.setupGetParameterMap(requestParams);
		MaverickContext mockMavCtx = new MaverickContext(
			null, request, response);
		try
		{
			ctrl.init(null);
			ctrl.go(mockMavCtx);
			TestBean bean = ctrl.getTestBean();
			assertEquals(FormBeanCtrlBase.SUCCESS, ctrl.getView());
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
		}
		catch (ServletException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}	
	}
	
	/**
	 * Test the population of arrays when the are in the request as 
	 * array = {value1, value2}
	 */
	public void testRequestStringArrayPopulation()
	{
		TestCtrl ctrl = new TestCtrl();
		Map requestParams = new HashMap();
		requestParams.put("testStringArray1", new String[] {"arrayelem0", "arrayelem1"});
		request.setupGetParameterMap(requestParams);
		MaverickContext mockMavCtx = new MaverickContext(
			null, request, response);
		try
		{
			ctrl.init(null);
			ctrl.go(mockMavCtx);
			TestBean bean = ctrl.getTestBean();
			assertEquals(FormBeanCtrlBase.SUCCESS, ctrl.getView());
			String[] testStringArray1 = bean.getTestStringArray1();
			assertNotNull(testStringArray1);
			assertEquals(2, testStringArray1.length);
			assertEquals("arrayelem0", testStringArray1[0]);
			assertEquals("arrayelem1", testStringArray1[1]);
		}
		catch (ServletException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}		
	}
	
	/**
	 * Test the population of arrays when the are in the request as 
	 * array[0] = value1
	 * array[1] = value2
	 */
	public void testStringArrayPopulation()
	{
		TestCtrl ctrl = new TestCtrl();
		Map requestParams = new HashMap();
		requestParams.put("testStringArray2[0]", "newval0");
		requestParams.put("testStringArray2[1]", "newval1");
		request.setupGetParameterMap(requestParams);
		MaverickContext mockMavCtx = new MaverickContext(
			null, request, response);
		try
		{
			ctrl.init(null);
			ctrl.go(mockMavCtx);
			TestBean bean = ctrl.getTestBean();
			assertEquals(FormBeanCtrlBase.SUCCESS, ctrl.getView());			
			String[] testStringArray2 = bean.getTestStringArray2();
			assertNotNull(testStringArray2);
			assertEquals(2, testStringArray2.length);
			assertEquals("newval0", testStringArray2[0]);
			assertEquals("newval1", testStringArray2[1]);
		}
		catch (ServletException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}	
	}
	
	public void testStringMapPopulation()
	{
		TestCtrl ctrl = new TestCtrl();
		Map requestParams = new HashMap();
		requestParams.put("testMap['key1']", "val1");
		requestParams.put("testMap['key2']", "val2");
		request.setupGetParameterMap(requestParams);
		MaverickContext mockMavCtx = new MaverickContext(
			null, request, response);
		try
		{
			ctrl.init(null);
			ctrl.go(mockMavCtx);
			TestBean bean = ctrl.getTestBean();
			assertEquals(FormBeanCtrlBase.SUCCESS, ctrl.getView());
			Map map = bean.getTestMap();
			assertNotNull(map);
			assertEquals(2, map.size());
			assertEquals("val1", map.get("key1"));
			assertEquals("val2", map.get("key2"));
		}
		catch (ServletException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}	
	}
	
	public void testPopulationWithCustomPopulators()
	{
		TestCtrl ctrl = new TestCtrl();
		Map requestParams = new HashMap();
		requestParams.put("uppercaseTest", "this once was lower case");
		requestParams.put("ignore", "this should never come through");
		request.setupGetParameterMap(requestParams);
		MaverickContext mockMavCtx = new MaverickContext(
			null, request, response);
		try
		{
			ctrl.init(null);
			ctrl.go(mockMavCtx);
			TestBean bean = ctrl.getTestBean();
			assertEquals(FormBeanCtrlBase.SUCCESS, ctrl.getView());
			assertEquals("THIS ONCE WAS LOWER CASE", bean.getUppercaseTest());
			assertEquals("unchanged", bean.getIgnore());
		}
		catch (ServletException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}	
	}
	
	public void testPopulationWithCustomPopulatorByRegexMatch()
	{
		TestCtrl ctrl = new TestCtrl();
		Map requestParams = new HashMap();
		requestParams.put("ignoreByRegex", "this should never come through");
		request.setupGetParameterMap(requestParams);
		MaverickContext mockMavCtx = new MaverickContext(
			null, request, response);
		try
		{
			ctrl.init(null);
			ctrl.go(mockMavCtx);
			TestBean bean = ctrl.getTestBean();
			assertEquals(FormBeanCtrlBase.SUCCESS, ctrl.getView());
			assertEquals("unchanged (regex)", bean.getIgnoreByRegex());
		}
		catch (ServletException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}	
	}
	
	/**
	 * Test the population of indexed properties.
	 *
	 */
	public void testPopulationWithListProperties()
	{
		TestCtrl ctrl = new TestCtrl();
		Map requestParams = new HashMap();
		requestParams.put("listProperty[0]", "newval0");
		requestParams.put("listProperty[1]", "newval1");
		request.setupGetParameterMap(requestParams);
		MaverickContext mockMavCtx = new MaverickContext(
			null, request, response);
		try
		{
			ctrl.init(null);
			ctrl.go(mockMavCtx);
			TestBean bean = ctrl.getTestBean();
			assertEquals(FormBeanCtrlBase.SUCCESS, ctrl.getView());
			
			List listProperty = bean.getListProperty();
			assertNotNull(listProperty);
			assertEquals(2, listProperty.size());
			assertEquals("newval0", listProperty.get(0));
			assertEquals("newval1", listProperty.get(1));
		}
		catch (ServletException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	/**
	 * Test the population of multidimensional mapped properties.
	 *
	 */
	public void testPopulationWithMultiDimensionalMappedProperties()
	{
		TestCtrl ctrl = new TestCtrl();
		Map requestParams = new HashMap();
		requestParams.put("multiDimensionalMap['one']['one']", "newval0");
		requestParams.put("multiDimensionalMap['one']['two']", "newval1");
		requestParams.put("multiDimensionalMap['one']['three']", "newval2");
		requestParams.put("multiDimensionalMap['two']['one']", "newval3");
		request.setupGetParameterMap(requestParams);
		MaverickContext mockMavCtx = new MaverickContext(
			null, request, response);
		try
		{
			ctrl.init(null);
			ctrl.go(mockMavCtx);
			TestBean bean = ctrl.getTestBean();
			assertEquals(FormBeanCtrlBase.SUCCESS, ctrl.getView());
		
			Map listProperty = bean.getMultiDimensionalMap();
			assertNotNull(listProperty);
			assertEquals(2, listProperty.size());
			
			Map one = (Map)listProperty.get("one");				
			assertEquals(3, one.size());
			
			assertTrue(one.containsKey("one"));
			assertTrue(one.containsKey("two"));
			assertTrue(one.containsKey("three"));
			
			assertEquals("newval0", one.get("one"));
			assertEquals("newval1", one.get("two"));
			assertEquals("newval2", one.get("three"));
			
			Map two = (Map)listProperty.get("two");
			assertEquals(1, two.size());
			assertTrue(two.containsKey("one"));
			assertEquals("newval3", two.get("one"));
		}
		catch (ServletException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * Test the population of multidimensional List properties. 
	 *
	 */
	public void testPopulationWithMultiDimensionalListProperties()
	{
		TestCtrl ctrl = new TestCtrl();
		Map requestParams = new HashMap();
		requestParams.put("multiDimensionalList[0][0]", "newval0");
		requestParams.put("multiDimensionalList[0][1]", "newval1");
		requestParams.put("multiDimensionalList[0][2]", "newval2");
		requestParams.put("multiDimensionalList[1][0]", "newval0");
		request.setupGetParameterMap(requestParams);
		MaverickContext mockMavCtx = new MaverickContext(
			null, request, response);
		try
		{
			ctrl.init(null);
			ctrl.go(mockMavCtx);
			TestBean bean = ctrl.getTestBean();
			assertEquals(FormBeanCtrlBase.SUCCESS, ctrl.getView());
			
			List listProperty = bean.getMultiDimensionalList();
			assertNotNull(listProperty);
			assertEquals(2, listProperty.size());
			
			List one = (List)listProperty.get(0);
			assertEquals(3, one.size());
			
			List two = (List)listProperty.get(1);
			assertEquals(1, two.size());
			
			assertTrue(listProperty.get(0).getClass().getName() + 
				" with value " + listProperty.get(0)
				, listProperty.get(0) instanceof List);
			assertTrue(listProperty.get(1).getClass().getName() + 
				" with value " + listProperty.get(0)
				, listProperty.get(1) instanceof List);
		}
		catch (ServletException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	public void testError1()
	{
		TestCtrl ctrl = new TestCtrl();
		Map requestParams = new HashMap();
		requestParams.put("testDouble1", "wrong"); // test simple string
		request.setupGetParameterMap(requestParams);
		MaverickContext mockMavCtx = new MaverickContext(
			null, request, response);
		try
		{
			ctrl.init(null);
			ctrl.go(mockMavCtx);
			TestBean bean = ctrl.getTestBean();
			assertEquals(FormBeanCtrlBase.ERROR, ctrl.getView());
			assertNull(bean.getTestDouble1());

		}
		catch (ServletException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}	
	}
	
	
//	public void testGetMultiDimProperty()
//	{
//		TestBean bean = new TestBean();
//		Map map1 = new HashMap();
//		Map map2a = new HashMap();
//		map2a.put("key2a-1", "value2a-1");
//		map2a.put("key2a-2", "value2a-2");
//		Map map2b = new HashMap();
//		map2b.put("key2b-1", "value2b-1");
//		map1.put("map2a", map2a);
//		map1.put("map2b", map2b);
//		bean.setMultiDimensionalMap(map1);
//		try
//		{
//			Object result = Ognl.getValue("multiDimensionalMap[\"map2a\"][\"key2a-2\"]", bean);
//			assertEquals("value2a-2", result);
//			bean.setTestDate1(new Date());
//			result = Ognl.getValue("testDate1", bean);
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//		}
//	}
	
	
}
