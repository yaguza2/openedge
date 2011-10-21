package nl.openedge.modules.impl.menumodule;

import java.util.Map;

import javax.security.auth.AuthPermission;
import javax.security.auth.Subject;

import nl.openedge.access.AccessHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Filters on authorizations with Access/ JAAS.
 * 
 * @author Eelco Hillenius
 */
public final class AuthorizationFilter extends AbstractMenuFilter implements SessionScopeMenuFilter
{
	private static Logger log = LoggerFactory.getLogger(AuthorizationFilter.class);

	@Override
	public boolean accept(MenuItem menuItem, Map<Object, Object> context)
	{
		boolean accepted = false; // default is to deny

		String link = menuItem.getLink();
		int ix = link.indexOf('?'); // strip parameter and '?'
		if (ix != -1)
		{
			link = link.substring(0, ix);
		}

		Subject subject = (Subject) context.get(MenuFilter.CONTEXT_KEY_SUBJECT);
		try
		{
			AccessHelper.checkPermissionForSubject(new AuthPermission(link), subject);

			// no exception: subject is authorized
			// create new work node

			accepted = true; // ok
		}
		catch (SecurityException se)
		{
			// no permission
			log.debug(subject + " has no permission for " + menuItem);
		}

		return accepted;
	}
}
