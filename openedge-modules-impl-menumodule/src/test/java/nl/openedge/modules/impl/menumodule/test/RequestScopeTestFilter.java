package nl.openedge.modules.impl.menumodule.test;

import java.util.Map;

import nl.openedge.modules.impl.menumodule.AbstractMenuFilter;
import nl.openedge.modules.impl.menumodule.MenuItem;
import nl.openedge.modules.impl.menumodule.RequestScopeMenuFilter;

public final class RequestScopeTestFilter extends AbstractMenuFilter implements
		RequestScopeMenuFilter
{
	public static final String TEST_CONTEXT_KEY = "testkey";

	@Override
	public boolean accept(MenuItem menuItem, Map<Object, Object> context)
	{
		return context.get(TEST_CONTEXT_KEY) != null && menuItem.getLink().equals("/zoeken.m");
	}
}
