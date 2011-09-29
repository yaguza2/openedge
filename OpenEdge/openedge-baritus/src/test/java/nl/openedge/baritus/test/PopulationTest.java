package nl.openedge.baritus.test;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;

import nl.openedge.baritus.ExecutionParams;
import nl.openedge.baritus.FormBeanContext;
import nl.openedge.baritus.FormBeanCtrlBase;
import nl.openedge.baritus.test.mock.MockHttpServletRequest;
import nl.openedge.baritus.test.mock.MockHttpServletResponse;

import org.infohazard.maverick.flow.MaverickContext;
import org.junit.Before;
import org.junit.Test;

import com.mockobjects.servlet.MockHttpSession;
import com.mockobjects.servlet.MockRequestDispatcher;
import com.mockobjects.servlet.MockServletConfig;
import com.mockobjects.servlet.MockServletContext;

/**
 * Testcase for population of form beans and interceptors.
 * 
 * @author Eelco Hillenius
 * @author Sander Hofstee
 */
@SuppressWarnings("all")
public class PopulationTest
{
	private Locale dutch = new Locale("nl", "NL");

	private MockRequestDispatcher requestDispatcher = null;

	private MockServletContext servletContext = null;

	private MockServletConfig servletConfig = null;

	private MockHttpSession session = null;

	private MockHttpServletResponse response = null;

	private MockHttpServletRequest request = null;

	@Before
	public void setUp()
	{
		this.requestDispatcher = new MockRequestDispatcher();
		this.servletContext = new MockServletContext();
		this.servletContext.setupGetRequestDispatcher(requestDispatcher);
		this.servletConfig = new MockServletConfig();
		this.servletConfig.setServletContext(servletContext);
		this.session = new MockHttpSession();
		this.session.setupGetAttribute(FormBeanCtrlBase.SESSION_KEY_CURRENT_LOCALE, dutch);

		this.session.setupServletContext(servletContext);
		this.response = new MockHttpServletResponse();
		this.request = new MockHttpServletRequest();
		this.request.setupGetAttribute("__formBeanContext");
		this.request.setSession(session);
		this.request.setupGetRequestDispatcher(requestDispatcher);
	}

	@Test
	public void integerPopulationAndBeforePerformInterceptor()
	{
		MockCtrl ctrl = new MockCtrl();
		Map requestParams = new HashMap();
		requestParams.put("testInteger1", "1"); // test simple string
		requestParams.put("testInteger2", new String[] {"2"}); // test string array
		request.setupGetParameterMap(requestParams);
		MaverickContext mockMavCtx = new MaverickContext(null, request, response);

		try
		{
			ctrl.go(mockMavCtx);
			MockBean bean = ctrl.getTestBean();
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

	@Test
	public void longPopulation()
	{
		MockCtrl ctrl = new MockCtrl();
		Map requestParams = new HashMap();
		requestParams.put("testLong1", "1"); // test simple string
		requestParams.put("testLong2", new String[] {"2"}); // test string array
		request.setupGetParameterMap(requestParams);
		MaverickContext mockMavCtx = new MaverickContext(null, request, response);
		try
		{
			ctrl.go(mockMavCtx);
			MockBean bean = ctrl.getTestBean();
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

	@Test
	public void doublePopulationAndLocalizedDisplayProperty()
	{
		MockCtrl ctrl = new MockCtrl();
		Map requestParams = new HashMap();
		requestParams.put("testDouble1", "1,1"); // test simple string
		requestParams.put("testDouble2", new String[] {"1,2"}); // test string array
		request.setupGetParameterMap(requestParams);
		MaverickContext mockMavCtx = new MaverickContext(null, request, response);
		try
		{
			ctrl.go(mockMavCtx);
			MockBean bean = ctrl.getTestBean();
			assertEquals(FormBeanCtrlBase.SUCCESS, ctrl.getView());
			assertNotNull(bean.getTestDouble1());
			assertEquals(new Double(1.1), bean.getTestDouble1());
			assertNotNull(bean.getTestDouble2());
			assertEquals(new Double(1.2), bean.getTestDouble2());
			FormBeanContext formBeanContext = ctrl.getFormBeanContext();
			assertEquals("dutch locale should be used for formatting a double property", "1,1",
				formBeanContext.displayProperty("testDouble1"));
		}
		catch (ServletException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void datePopulation()
	{
		MockCtrl ctrl = new MockCtrl();
		Map requestParams = new HashMap();
		requestParams.put("testDate1", "20-02-2004"); // test simple string
		requestParams.put("testDate2", new String[] {"21-03-2005"}); // test string array
		request.setupGetParameterMap(requestParams);
		MaverickContext mockMavCtx = new MaverickContext(null, request, response);
		try
		{
			ctrl.go(mockMavCtx);
			MockBean bean = ctrl.getTestBean();
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
	 * Test the population of arrays when the parameters are in the request as array =
	 * {value1, value2}
	 */
	@Test
	public void requestStringArrayPopulation()
	{
		MockCtrl ctrl = new MockCtrl();
		Map requestParams = new HashMap();
		requestParams.put("testStringArray1", new String[] {"arrayelem0", "arrayelem1"});
		request.setupGetParameterMap(requestParams);
		MaverickContext mockMavCtx = new MaverickContext(null, request, response);
		try
		{
			ctrl.go(mockMavCtx);
			MockBean bean = ctrl.getTestBean();
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

	@Test
	public void stringMapPopulation()
	{
		MockCtrl ctrl = new MockCtrl();
		Map requestParams = new HashMap();
		requestParams.put("testMap['key1']", "val1");
		requestParams.put("testMap['key2']", "val2");
		request.setupGetParameterMap(requestParams);
		MaverickContext mockMavCtx = new MaverickContext(null, request, response);
		try
		{
			ctrl.go(mockMavCtx);
			MockBean bean = ctrl.getTestBean();
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

	@Test
	public void populationWithCustomPopulators()
	{
		MockCtrl ctrl = new MockCtrl();
		Map requestParams = new HashMap();
		requestParams.put("uppercaseTest", "this once was lower case");
		requestParams.put("ignore", "this should never come through");
		request.setupGetParameterMap(requestParams);
		MaverickContext mockMavCtx = new MaverickContext(null, request, response);
		try
		{
			ctrl.go(mockMavCtx);
			MockBean bean = ctrl.getTestBean();
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

	@Test
	public void populationWithCustomPopulatorByRegexMatch()
	{
		MockCtrl ctrl = new MockCtrl();
		Map requestParams = new HashMap();
		requestParams.put("ignoreByRegex", "this should never come through");
		request.setupGetParameterMap(requestParams);
		MaverickContext mockMavCtx = new MaverickContext(null, request, response);
		try
		{
			ctrl.go(mockMavCtx);
			MockBean bean = ctrl.getTestBean();
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
	@Test
	public void populationWithListProperties()
	{
		MockCtrl ctrl = new MockCtrl();
		Map requestParams = new HashMap();
		requestParams.put("listProperty[0]", "newval0");
		requestParams.put("listProperty[1]", "newval1");
		request.setupGetParameterMap(requestParams);
		MaverickContext mockMavCtx = new MaverickContext(null, request, response);
		try
		{
			ctrl.go(mockMavCtx);
			MockBean bean = ctrl.getTestBean();
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
	@Test
	public void populationWithMultiDimensionalMappedProperties()
	{
		MockCtrl ctrl = new MockCtrl();
		Map requestParams = new HashMap();
		requestParams.put("multiDimensionalMap['one']['one']", "newval0");
		requestParams.put("multiDimensionalMap['one']['two']", "newval1");
		requestParams.put("multiDimensionalMap['one']['three']", "newval2");
		requestParams.put("multiDimensionalMap['two']['one']", "newval3");
		request.setupGetParameterMap(requestParams);
		MaverickContext mockMavCtx = new MaverickContext(null, request, response);
		try
		{
			ctrl.go(mockMavCtx);
			MockBean bean = ctrl.getTestBean();
			assertEquals(FormBeanCtrlBase.SUCCESS, ctrl.getView());

			Map listProperty = bean.getMultiDimensionalMap();
			assertNotNull(listProperty);
			assertEquals(2, listProperty.size());

			Map one = (Map) listProperty.get("one");
			assertEquals(3, one.size());

			assertTrue(one.containsKey("one"));
			assertTrue(one.containsKey("two"));
			assertTrue(one.containsKey("three"));

			assertEquals("newval0", one.get("one"));
			assertEquals("newval1", one.get("two"));
			assertEquals("newval2", one.get("three"));

			Map two = (Map) listProperty.get("two");
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
	@Test
	public void populationWithMultiDimensionalListProperties()
	{
		MockCtrl ctrl = new MockCtrl();
		Map requestParams = new HashMap();
		requestParams.put("multiDimensionalList[0][0]", "newval0");
		requestParams.put("multiDimensionalList[0][1]", "newval1");
		requestParams.put("multiDimensionalList[0][2]", "newval2");
		requestParams.put("multiDimensionalList[1][0]", "newval0");
		request.setupGetParameterMap(requestParams);
		MaverickContext mockMavCtx = new MaverickContext(null, request, response);
		try
		{
			ctrl.go(mockMavCtx);
			MockBean bean = ctrl.getTestBean();
			assertEquals(FormBeanCtrlBase.SUCCESS, ctrl.getView());

			List listProperty = bean.getMultiDimensionalList();
			assertNotNull(listProperty);
			assertEquals(2, listProperty.size());

			List one = (List) listProperty.get(0);
			assertEquals(3, one.size());

			List two = (List) listProperty.get(1);
			assertEquals(1, two.size());

			assertTrue(
				listProperty.get(0).getClass().getName() + " with value " + listProperty.get(0),
				listProperty.get(0) instanceof List);
			assertTrue(
				listProperty.get(1).getClass().getName() + " with value " + listProperty.get(0),
				listProperty.get(1) instanceof List);
		}
		catch (ServletException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void error1()
	{
		MockCtrl ctrl = new MockCtrl();
		Map requestParams = new HashMap();
		requestParams.put("testDouble1", "wrong"); // test simple string
		request.setupGetParameterMap(requestParams);
		MaverickContext mockMavCtx = new MaverickContext(null, request, response);
		try
		{
			ctrl.go(mockMavCtx);
			MockBean bean = ctrl.getTestBean();
			assertEquals(FormBeanCtrlBase.ERROR, ctrl.getView());
			assertNull(bean.getTestDouble1());

		}
		catch (ServletException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void objectFromRequestAttributesPopulation()
	{
		MockCtrl ctrl = new MockCtrl();

		Map requestParams = new HashMap();
		MockObject testObject = new MockObject();
		testObject.setTestString("a test");
		request.setAttribute("testObject", testObject);
		request.setupGetParameterMap(requestParams);

		MaverickContext mockMavCtx = new MaverickContext(null, request, response);
		try
		{
			ctrl.go(mockMavCtx);
			MockBean bean = ctrl.getTestBean();
			assertEquals(FormBeanCtrlBase.SUCCESS, ctrl.getView());
			assertNotNull(bean.getTestObject());
			assertEquals("a test", bean.getTestObject().getTestString());
		}
		catch (ServletException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	// OGNL!
	@Test
	public void strictParsing()
	{
		MockCtrl ctrl = new MockCtrl();
		Map requestParams = new HashMap();
		requestParams.put("not-a-valid-name", "foo"); // test simple string
		request.setupGetParameterMap(requestParams);
		MaverickContext mockMavCtx = new MaverickContext(null, request, response);
		try
		{
			ctrl.go(mockMavCtx);
			MockBean bean = ctrl.getTestBean();
			assertEquals(FormBeanCtrlBase.ERROR, ctrl.getView()); // should fail by
																	// default
		}
		catch (ServletException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	// OGNL!
	@Test
	public void nonStrictParsing()
	{
		MockCtrl ctrl = new MockCtrl();
		ExecutionParams params = ctrl.getExecutionParams(null);
		params.setStrictPopulationMode(false);
		ctrl.fixExecutionParams(params);
		Map requestParams = new HashMap();
		requestParams.put("not-a-valid-name", "foo"); // test simple string
		request.setupGetParameterMap(requestParams);
		MaverickContext mockMavCtx = new MaverickContext(null, request, response);
		try
		{
			ctrl.go(mockMavCtx);
			MockBean bean = ctrl.getTestBean();
			assertEquals(FormBeanCtrlBase.SUCCESS, ctrl.getView()); // should be
																	// succesfull now
		}
		catch (ServletException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void trimString1()
	{
		MockCtrl ctrl = new MockCtrl();
		Map requestParams = new HashMap();
		requestParams.put("testTrimString", "    tobetrimmed     ");
		request.setupGetParameterMap(requestParams);
		MaverickContext mockMavCtx = new MaverickContext(null, request, response);
		try
		{
			ctrl.go(mockMavCtx);
			MockBean bean = ctrl.getTestBean();
			assertEquals(FormBeanCtrlBase.SUCCESS, ctrl.getView());
			assertNotNull(bean.getTestTrimString());
			assertEquals("tobetrimmed", bean.getTestTrimString());
		}
		catch (ServletException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void trimString2()
	{
		MockCtrl ctrl = new MockCtrl();
		Map requestParams = new HashMap();
		requestParams.put("testTrimStringArray[0]", "    tobetrimmed     ");
		request.setupGetParameterMap(requestParams);
		MaverickContext mockMavCtx = new MaverickContext(null, request, response);
		try
		{
			ctrl.go(mockMavCtx);
			MockBean bean = ctrl.getTestBean();
			assertEquals(FormBeanCtrlBase.SUCCESS, ctrl.getView());
			assertNotNull(bean.getTestTrimStringArray());
			assertNotNull(bean.getTestTrimStringArray()[0]);
			assertEquals("tobetrimmed", bean.getTestTrimStringArray()[0]);
		}
		catch (ServletException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void trimString3()
	{
		MockCtrl ctrl = new MockCtrl();
		ExecutionParams params = ctrl.getExecutionParams(null);
		params.setTrimStringInputValues(false);
		ctrl.fixExecutionParams(params);
		Map requestParams = new HashMap();
		requestParams.put("testTrimString", "    notbetrimmed     ");
		request.setupGetParameterMap(requestParams);
		MaverickContext mockMavCtx = new MaverickContext(null, request, response);
		try
		{
			ctrl.go(mockMavCtx);
			MockBean bean = ctrl.getTestBean();
			assertEquals(FormBeanCtrlBase.SUCCESS, ctrl.getView());
			assertNotNull(bean.getTestTrimString());
			assertEquals("    notbetrimmed     ", bean.getTestTrimString());
		}
		catch (ServletException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void trimString4()
	{
		MockCtrl ctrl = new MockCtrl();
		ExecutionParams params = ctrl.getExecutionParams(null);
		params.setTrimStringInputValues(false);
		ctrl.fixExecutionParams(params);
		Map requestParams = new HashMap();
		requestParams.put("testTrimStringArray[0]", "    notbetrimmed     ");
		request.setupGetParameterMap(requestParams);
		MaverickContext mockMavCtx = new MaverickContext(null, request, response);
		try
		{
			ctrl.go(mockMavCtx);
			MockBean bean = ctrl.getTestBean();
			assertEquals(FormBeanCtrlBase.SUCCESS, ctrl.getView());
			assertNotNull(bean.getTestTrimStringArray());
			assertNotNull(bean.getTestTrimStringArray()[0]);
			assertEquals("    notbetrimmed     ", bean.getTestTrimStringArray()[0]);
		}
		catch (ServletException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * Tests that a request attribute overrides a request parameter, and that the
	 * population with the request parameter is never tried when there is an overruling
	 * parameter.
	 */
	@Test
	public void requestParamAndRequestAttribPopulation()
	{
		MockCtrl ctrl = new MockCtrl();
		Map requestParams = new HashMap();
		requestParams.put("testInteger1", "not a number at all"); // test invalid value
		request.setupGetParameterMap(requestParams);
		request.setAttribute("testInteger1", "1"); // this is valid, and should
		// override the non-valid request parameter. Hence, population
		// should not result in errors.
		MaverickContext mockMavCtx = new MaverickContext(null, request, response);
		try
		{
			ctrl.go(mockMavCtx);
			MockBean bean = ctrl.getTestBean();
			assertEquals(FormBeanCtrlBase.SUCCESS, ctrl.getView());
			assertNotNull(bean.getTestInteger1());
			assertEquals(new Integer(1), bean.getTestInteger1());
		}
		catch (ServletException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
