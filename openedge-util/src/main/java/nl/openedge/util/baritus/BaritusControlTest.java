package nl.openedge.util.baritus;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Locale;

import junit.framework.TestCase;
import nl.openedge.baritus.ConverterRegistry;
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
 * Base class for testcases that are used to test controlls, validators etc. Note that
 * allthough this class depends on Mock Objects, its actual usage differs from the
 * standard mock objects way. Instead of the white box approach of mock objects (with
 * recording and playback), you can use these mock objects in a black box fashion, ie use
 * them as if being in a real web environment.
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
	 * Sets up the fixture; use instead of setUp(). This method is called after the
	 * finalized setUp method is called.
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
	 * Breaks down the fixture; use instead of breakDown(). This method is called after
	 * the finalized breakDown method is called.
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
