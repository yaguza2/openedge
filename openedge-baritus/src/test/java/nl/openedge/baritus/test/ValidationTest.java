package nl.openedge.baritus.test;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;

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
 * Testcase for population of form beans and interceptors
 * 
 * @author Eelco Hillenius
 */
@SuppressWarnings("all")
public class ValidationTest
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
	public void testValidFieldValidation()
	{
		MockCtrl ctrl = new MockCtrl();
		Map requestParams = new HashMap();
		requestParams.put("toValidate1", "validValue");
		request.setupGetParameterMap(requestParams);
		MaverickContext mockMavCtx = new MaverickContext(null, request, response);
		try
		{
			ctrl.go(mockMavCtx);
			MockBean bean = ctrl.getTestBean();
			assertEquals(FormBeanCtrlBase.SUCCESS, ctrl.getView());
		}
		catch (ServletException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testNonValidFieldValidation1()
	{
		MockCtrl ctrl = new MockCtrl();
		Map requestParams = new HashMap();
		requestParams.put("toValidate1", "nonValidValue");
		request.setupGetParameterMap(requestParams);
		MaverickContext mockMavCtx = new MaverickContext(null, request, response);
		try
		{
			ctrl.go(mockMavCtx);
			MockBean bean = ctrl.getTestBean();
			assertEquals(FormBeanCtrlBase.ERROR, ctrl.getView());
		}
		catch (ServletException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testNonValidFieldValidation2()
	{
		MockCtrl ctrl = new MockCtrl();
		Map requestParams = new HashMap();
		requestParams.put("toValidate1", "kill");
		request.setupGetParameterMap(requestParams);
		MaverickContext mockMavCtx = new MaverickContext(null, request, response);
		try
		{
			ctrl.go(mockMavCtx);
			MockBean bean = ctrl.getTestBean();
			assertEquals(FormBeanCtrlBase.ERROR, ctrl.getView());
		}
		catch (ServletException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testIndexedFieldValidation1()
	{
		MockCtrl ctrl = new MockCtrl();
		Map requestParams = new HashMap();
		requestParams.put("toValidate2[0]", "validValue");
		requestParams.put("toValidate2[1]", "nonValidValue");
		request.setupGetParameterMap(requestParams);
		MaverickContext mockMavCtx = new MaverickContext(null, request, response);
		try
		{
			ctrl.go(mockMavCtx);
			MockBean bean = ctrl.getTestBean();
			assertEquals(FormBeanCtrlBase.ERROR, ctrl.getView());
			FormBeanContext fbc = ctrl.getFormBeanContext();
		}
		catch (ServletException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testIndexedFieldValidation2()
	{
		MockCtrl ctrl = new MockCtrl();
		Map requestParams = new HashMap();
		requestParams.put("toValidate3[0]", "nonValidValue");
		request.setupGetParameterMap(requestParams);
		MaverickContext mockMavCtx = new MaverickContext(null, request, response);
		try
		{
			ctrl.go(mockMavCtx);
			MockBean bean = ctrl.getTestBean();
			assertEquals(FormBeanCtrlBase.ERROR, ctrl.getView());
		}
		catch (ServletException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testIndexedFieldValidation3()
	{
		MockCtrl ctrl = new MockCtrl();
		Map requestParams = new HashMap();
		requestParams.put("toValidate3[0]", "validValue");
		requestParams.put("toValidate3[1]", "nonValidValue"); // should not be checked
		request.setupGetParameterMap(requestParams);
		MaverickContext mockMavCtx = new MaverickContext(null, request, response);
		try
		{
			ctrl.go(mockMavCtx);
			MockBean bean = ctrl.getTestBean();
			assertEquals(FormBeanCtrlBase.SUCCESS, ctrl.getView());
		}
		catch (ServletException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testFormValidation1()
	{
		MockCtrl ctrl = new MockCtrl();
		Map requestParams = new HashMap();
		requestParams.put("toValidate4", "validValue");
		request.setupGetParameterMap(requestParams);
		MaverickContext mockMavCtx = new MaverickContext(null, request, response);
		try
		{
			ctrl.go(mockMavCtx);
			MockBean bean = ctrl.getTestBean();
			assertEquals(FormBeanCtrlBase.SUCCESS, ctrl.getView());
		}
		catch (ServletException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testFormValidation2()
	{
		MockCtrl ctrl = new MockCtrl();
		Map requestParams = new HashMap();
		requestParams.put("toValidate4", "nonValidValue");
		request.setupGetParameterMap(requestParams);
		MaverickContext mockMavCtx = new MaverickContext(null, request, response);
		try
		{
			ctrl.go(mockMavCtx);
			MockBean bean = ctrl.getTestBean();
			assertEquals(FormBeanCtrlBase.ERROR, ctrl.getView());
		}
		catch (ServletException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testFormValidation3()
	{
		MockCtrl ctrl = new MockCtrl();
		Map requestParams = new HashMap();
		requestParams.put("toValidate4", "kill");
		request.setupGetParameterMap(requestParams);
		MaverickContext mockMavCtx = new MaverickContext(null, request, response);
		try
		{
			ctrl.go(mockMavCtx);
			MockBean bean = ctrl.getTestBean();
			assertEquals(FormBeanCtrlBase.ERROR, ctrl.getView());
		}
		catch (ServletException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
