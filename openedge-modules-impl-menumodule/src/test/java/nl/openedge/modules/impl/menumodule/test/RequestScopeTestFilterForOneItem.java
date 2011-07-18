package nl.openedge.modules.impl.menumodule.test;

import java.util.Map;

import nl.openedge.modules.impl.menumodule.AbstractMenuFilter;
import nl.openedge.modules.impl.menumodule.MenuItem;
import nl.openedge.modules.impl.menumodule.RequestScopeMenuFilter;

public final class RequestScopeTestFilterForOneItem extends AbstractMenuFilter implements
		RequestScopeMenuFilter
{
	public final static String TEST_CONTEXT_KEY = "nodeleveltestkey";

	@Override
	public boolean accept(MenuItem menuItem, Map<Object, Object> context)
	{
		context.put(TEST_CONTEXT_KEY, new Object());
		return true;
	}
}
