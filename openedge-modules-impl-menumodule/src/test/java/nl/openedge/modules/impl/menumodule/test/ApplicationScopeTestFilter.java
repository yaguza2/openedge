package nl.openedge.modules.impl.menumodule.test;

import java.util.Map;

import nl.openedge.modules.impl.menumodule.AbstractMenuFilter;
import nl.openedge.modules.impl.menumodule.ApplicationScopeMenuFilter;
import nl.openedge.modules.impl.menumodule.MenuItem;

/**
 * @author Eelco Hillenius
 */
public final class ApplicationScopeTestFilter extends AbstractMenuFilter implements
		ApplicationScopeMenuFilter
{
	@Override
	public boolean accept(MenuItem menuItem, Map<Object, Object> context)
	{
		boolean accepted = true;

		if (menuItem.getLink().equals("/admin.onderhoud.filtertest.m"))
		{
			accepted = false;
		}

		return accepted;
	}

}
