package nl.openedge.modules.impl.menumodule;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Always disables all.
 * 
 * @author Sander Hofstee
 */
public final class DisabledFilter extends AbstractMenuFilter implements RequestScopeMenuFilter
{
	private static Logger log = LoggerFactory.getLogger(DisabledFilter.class);

	@Override
	public boolean accept(MenuItem menuItem, Map<Object, Object> context)
	{
		boolean accepted = true;

		log.debug("DisabledFilter:" + menuItem.getTag() + " is disabled.");

		menuItem.setEnabled(false);

		return accepted;
	}

}
