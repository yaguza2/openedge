package nl.openedge.access;

import java.io.IOException;
import java.io.Serializable;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

/**
 * AccessCallbackHandler has constructor that takes a username and password so its
 * handle() method does not have to prompt the user for input. Useful for server-side
 * applications.
 * <p>
 * Besides this the default username/ password, additional fields can be set using the
 * delegate construction
 * 
 * @author Eelco Hillenius
 */
public final class AccessCallbackHandler implements CallbackHandler, Serializable
{
	private static final long serialVersionUID = 1L;

	private String username;

	private char[] password;

	/**
	 * Construct.
	 */
	public AccessCallbackHandler()
	{
	}

	/**
	 * Creates a callback handler with the give username and password.
	 * 
	 * @param user
	 *            username
	 * @param pass
	 *            password
	 */
	public AccessCallbackHandler(String user, String pass)
	{

		this.username = user;
		this.password = (pass != null) ? pass.toCharArray() : null;
	}

	/**
	 * Handles the specified set of Callbacks. Uses the username and password that were
	 * supplied to our constructor to popluate the Callbacks.
	 * 
	 * This class supports NameCallback and PasswordCallback.
	 * 
	 * @param callbacks
	 *            the callbacks to handle
	 * @throws IOException
	 *             if an input or output error occurs.
	 * @throws UnsupportedCallbackException
	 *             if the callback is not an instance of NameCallback or PasswordCallback
	 */
	@Override
	public void handle(Callback[] callbacks) throws java.io.IOException,
			UnsupportedCallbackException
	{

		for (int i = 0; i < callbacks.length; i++)
		{
			if (callbacks[i] instanceof NameCallback)
			{
				((NameCallback) callbacks[i]).setName(username);
			}
			else if (callbacks[i] instanceof PasswordCallback)
			{
				((PasswordCallback) callbacks[i]).setPassword(password);
			}
			else
			{
				throw new UnsupportedCallbackException(callbacks[i], "Callback class not supported");
			}
		}
	}

	/**
	 * Clears out password state.
	 */
	public void clearPassword()
	{
		if (password != null)
		{
			for (int i = 0; i < password.length; i++)
			{
				password[i] = ' ';
			}
			password = null;
		}
	}

}
