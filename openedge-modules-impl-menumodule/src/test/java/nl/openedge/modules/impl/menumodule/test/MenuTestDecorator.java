package nl.openedge.modules.impl.menumodule.test;

import junit.extensions.TestSetup;
import junit.framework.Test;
import nl.openedge.access.AccessHelper;
import nl.openedge.modules.JDOMConfigurator;

/**
 * Test decorator for MenuTest.
 */
public final class MenuTestDecorator extends TestSetup
{
	public MenuTestDecorator(Test test)
	{
		super(test);
	}

	@Override
	protected void setUp() throws Exception
	{
		setUpModules();
		setUpAccessFactory();
	}

	private void setUpModules() throws Exception
	{
		new JDOMConfigurator("test.oemodules.xml");
	}

	private void setUpAccessFactory() throws Exception
	{
		AccessHelper.reload(System.getProperty("configfile", "/test.oeaccess.properties"), "test");
	}
}
