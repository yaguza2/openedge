package nl.openedge.access;

import java.io.Serializable;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

/**
 * The SessionLoginContext extends the JAAS LoginContext, so that, when bound to an
 * HttpSession, it will execute logout() when the session times out. One can bind the
 * SessionLoginContext to the session, by calling
 * 
 * <pre>
 * session.setAttribute(&quot;loginContext&quot;, myLoginContext);
 * </pre>
 * 
 * @author E.F. Hillenius
 */
public final class SessionLoginContext extends LoginContext implements HttpSessionBindingListener,
		Serializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * We hebben dit nodig om de laatste naam te onthouden voor het deserialiseren. Het is
	 * een hack, maar de kans dat deze context instantie vaker wordt aangemaakt met een
	 * varierende naam is nihil.
	 */
	private static String name;

	public SessionLoginContext() throws LoginException
	{
		this(SessionLoginContext.name);
	}

	public SessionLoginContext(String name) throws LoginException
	{
		super(name);
		SessionLoginContext.name = name;
	}

	public SessionLoginContext(String name, CallbackHandler callbackHandler) throws LoginException
	{
		super(name, callbackHandler);
		SessionLoginContext.name = name;
	}

	public SessionLoginContext(String name, Subject subject) throws LoginException
	{
		super(name, subject);
		SessionLoginContext.name = name;
	}

	public SessionLoginContext(String name, Subject subject, CallbackHandler callbackHandler)
			throws LoginException
	{
		super(name, subject, callbackHandler);
		SessionLoginContext.name = name;
	}

	/**
	 * Notifies the object that it is being bound to a session and identifies the session.
	 * 
	 * @param event
	 *            the event that identifies the session
	 */
	@Override
	public void valueBound(HttpSessionBindingEvent event)
	{
	}

	/**
	 * Notifies the object that it is being unbound from a session and identifies the
	 * session.
	 * 
	 * @param event
	 *            the event that identifies the session
	 */
	@Override
	public void valueUnbound(HttpSessionBindingEvent event)
	{
		try
		{
			logout();
		}
		catch (LoginException ex)
		{
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}
}
