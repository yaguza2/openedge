package nl.openedge.access.impl;

import java.io.Serializable;
import java.security.Principal;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import nl.openedge.access.AccessCallbackHandler;
import nl.openedge.access.GroupPrincipal;
import nl.openedge.access.LoginDecorator;
import nl.openedge.access.RolePrincipal;
import nl.openedge.access.UserManagerModule;
import nl.openedge.access.UserPrincipal;
import nl.openedge.access.util.PasswordHelper;
import nl.openedge.modules.ComponentRepository;
import nl.openedge.modules.RepositoryFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LoginModuleImpl is a LoginModule that authenticates a given username/password
 * credential using Hibernate.
 * <p>
 * If the user entered a valid username and password, this <code>LoginModule</code>
 * associates a <code>UserPrincipal</code> and the relevant <code>RolePrincipals</code>
 * with the <code>Subject</code>.
 * <p>
 * This LoginModule also recognizes the following <code>Configuration</code> options:
 * 
 * <pre>
 *    useFirstPass   if, true, this LoginModule retrieves the
 *                   username and password from the module's shared state,
 *                   using "javax.security.auth.login.name" and
 *                   "javax.security.auth.login.password" as the respective
 *                   keys.  The retrieved values are used for authentication.
 *                   If authentication fails, no attempt for a retry is made,
 *                   and the failure is reported back to the calling
 *                   application.
 * 
 *    tryFirstPass   if, true, this LoginModule retrieves the
 *                   the username and password from the module's shared state,
 *                   using "javax.security.auth.login.name" and
 *                   "javax.security.auth.login.password" as the respective
 *                   keys.  The retrieved values are used for authentication.
 *                   If authentication fails, the module uses the
 *                   CallbackHandler to retrieve a new username and password,
 *                   and another attempt to authenticate is made.
 *                   If the authentication fails, the failure is reported
 *                   back to the calling application.
 * 
 *    storePass      if, true, this LoginModule stores the username and password
 *                   obtained from the CallbackHandler in the module's
 *                   shared state, using "javax.security.auth.login.name" and
 *                   "javax.security.auth.login.password" as the respective
 *                   keys.  This is not performed if existing values already
 *                   exist for the username and password in the shared state,
 *                   or if authentication fails.
 * 
 *    clearPass     if, true, this <code>LoginModule</code> clears the
 *                  username and password stored in the module's shared state
 *                  after both phases of authentication (login and commit)
 *                  have completed.
 * </pre>
 * 
 * @see javax.security.auth.spi.LoginModule
 * @author Eelco Hillenius
 */
public final class LoginModuleImpl implements LoginModule, Serializable
{
	private static final long serialVersionUID = 1L;

	protected CallbackHandler callbackHandler;

	protected Subject subject;

	protected Map<String, Object> sharedState;

	protected Map<String, String> options;

	protected boolean useFirstPass = false;

	protected boolean tryFirstPass = false;

	protected boolean storePass = false;

	protected boolean clearPass = false;

	protected String username;

	protected char[] password;

	// the authentication status
	protected boolean succeeded = false;

	protected boolean commitSucceeded = false;

	public static final String NAME = "javax.security.auth.login.name";

	public static final String PWD = "javax.security.auth.login.password";

	// temporary state
	protected Vector<Principal> tempCredentials;

	protected Vector<Principal> tempPrincipals;

	/** decorator if provided */
	protected LoginDecorator decorator = null;

	/** user manager, MUST be provided */
	protected UserManagerModule userManager = null;

	/** module alias for user manager: userManagerAlias */
	public final static String USER_MANAGER_ALIAS = "userManagerAlias";

	/* logger */
	protected Logger log = LoggerFactory.getLogger(this.getClass());

	/**
	 * Creates a login module.
	 */
	public LoginModuleImpl()
	{
		tempCredentials = new Vector<Principal>();
		tempPrincipals = new Vector<Principal>();
	}

	/**
	 * @see javax.security.auth.spi.LoginModule#initialize(javax.security.auth.Subject,
	 *      javax.security.auth.callback.CallbackHandler, java.util.Map, java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState,
			Map config)
	{
		// save the initial state
		this.callbackHandler = callbackHandler;
		this.subject = subject;
		this.sharedState = sharedState;
		this.options = config;

		String userManagerAlias = (String) config.get(USER_MANAGER_ALIAS);

		try
		{
			ComponentRepository mf = RepositoryFactory.getRepository();
			userManager = (UserManagerModule) mf.getComponent(userManagerAlias);
		}
		catch (Exception e)
		{
			log.error(e.getMessage(), e);
			return;
		}

		// read options
		tryFirstPass = "true".equalsIgnoreCase(options.get("tryFirstPass"));
		useFirstPass = "true".equalsIgnoreCase(options.get("useFirstPass"));
		storePass = "true".equalsIgnoreCase(options.get("storePass"));
		clearPass = "true".equalsIgnoreCase(options.get("clearPass"));

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if (classLoader == null)
		{
			classLoader = LoginModuleImpl.class.getClassLoader();
		}
		// see if there's a LoginDecorater configured
		String decoClass = options.get("decorator");
		if (decoClass != null)
		{
			// got one, try to instantiate
			try
			{
				Class< ? > cls = classLoader.loadClass(decoClass);
				this.decorator = (LoginDecorator) cls.newInstance();
			}
			catch (Exception e)
			{
				log.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * Prompt for username and password. Verify the password against the relevant name
	 * service.
	 * 
	 * @return true always, since this <code>LoginModule</code> should not be ignored.
	 * 
	 * @exception FailedLoginException
	 *                if the authentication fails.
	 *                <p>
	 * 
	 * @exception LoginException
	 *                if this <code>LoginModule</code> is unable to perform the
	 *                authentication.
	 */
	@Override
	public boolean login() throws LoginException
	{
		// attempt the authentication
		if (tryFirstPass)
		{
			try
			{
				// attempt the authentication by getting the
				// username and password from shared state
				attemptAuthentication(true);

				// authentication succeeded
				succeeded = true;

				log.debug("tryFirstPass succeeded");
				return true;
			}
			catch (LoginException le)
			{
				// authentication failed -- try again below by prompting
				cleanState();
				log.debug("tryFirstPass failed with: {}", le.toString());
			}
		}
		else if (useFirstPass)
		{
			try
			{
				// attempt the authentication by getting the
				// username and password from shared state
				attemptAuthentication(true);

				// authentication succeeded
				succeeded = true;

				log.debug("useFirstPass succeeded");
				return true;
			}
			catch (LoginException le)
			{
				// authentication failed
				cleanState();
				log.debug("useFirstPass failed");
				throw le;
			}
		}

		// attempt the authentication by prompting for the username and pwd
		try
		{
			attemptAuthentication(false);

			// authentication succeeded
			succeeded = true;
			log.debug("regular authentication succeeded");
			return true;
		}
		catch (LoginException le)
		{
			cleanState();
			log.debug("regular authentication failed");
			throw le;
		}
	}

	/**
	 * Attempt authentication
	 * 
	 * @param getPasswdFromSharedState
	 *            boolean that tells this method whether to retrieve the password from the
	 *            sharedState.
	 */
	public void attemptAuthentication(boolean getPasswdFromSharedState) throws LoginException
	{
		if (log.isDebugEnabled())
			log.debug("login for " + subject);

		if (callbackHandler == null)
			throw new LoginException("Error: no CallbackHandler available "
				+ "to gather authentication information from the user");

		try
		{
			// Setup default callback handlers.
			Callback[] callbacks =
				new Callback[] {new NameCallback("Username: "),
					new PasswordCallback("Password: ", false)};

			callbackHandler.handle(callbacks);

			username = ((NameCallback) callbacks[0]).getName();
			password = ((PasswordCallback) callbacks[1]).getPassword();

			((PasswordCallback) callbacks[1]).clearPassword();

			succeeded = validate(username, password);

			callbacks[0] = null;
			callbacks[1] = null;

			if (!succeeded)
				throw new LoginException(
					"Authentication failed: Invalid combination of username and password");

			// save input as shared state only if authentication succeeded
			if (storePass && !sharedState.containsKey(NAME) && !sharedState.containsKey(PWD))
			{
				sharedState.put(NAME, username);
				sharedState.put(PWD, password);
			}

			succeeded = true;
		}
		catch (LoginException ex)
		{
			throw ex;
		}
		catch (Exception ex)
		{
			succeeded = false;
			log.error(ex.getMessage(), ex);
			LoginException loginException = new LoginException(ex.getMessage());
			loginException.initCause(ex);
			throw loginException;
		}
	}

	/**
	 * Validate the given user and password.
	 * 
	 * @param username
	 *            the username to be authenticated.
	 * @param password
	 *            the password to be authenticated.
	 * @exception Exception
	 *                if the validation fails.
	 */
	protected boolean validate(String username, char[] password) throws Exception
	{
		boolean passwordMatch = false;
		UserPrincipal user = (UserPrincipal) userManager.getUser(username);

		if (user == null)
		{
			throw new LoginException("User " + username + " not found");
		}

		String cryptedPassword = new String(PasswordHelper.cryptPassword(password));
		passwordMatch = new String(cryptedPassword).equals(user.getPassword());

		if (passwordMatch)
		{
			this.tempPrincipals.add(user);
			Set<RolePrincipal> roles = userManager.listRolesForUser(user);
			if (roles != null)
			{
				this.tempPrincipals.addAll(roles);
			}
			Set<GroupPrincipal> groups = userManager.listGroupsForUser(user);
			if (groups != null)
			{
				this.tempPrincipals.addAll(groups);
			}
		}
		else
		{
			// passwords do NOT match!
		}

		return passwordMatch;
	}

	@Override
	public boolean commit() throws LoginException
	{
		log.debug("commit for {} (succeeded == {})", subject, succeeded);

		if (succeeded)
		{
			if (subject.isReadOnly())
				throw new LoginException("Subject is Readonly");

			try
			{
				Set<Principal> principals = subject.getPrincipals();
				if (decorator != null)
				{
					// decorate if a decorator was set this gives use the chance to
					// overload principals or maybe even add totally new ones
					Principal[] originals =
						tempPrincipals.toArray(new Principal[tempPrincipals.size()]);

					Principal[] decorated = decorator.decorate(originals);
					if (decorated != null)
					{
						for (int i = 0; i < decorated.length; i++)
						{
							principals.add(decorated[i]);
						}
					}
					// we now add all the prev loaded principals
					// if a principal was overloaded in the last block,
					// the Set.add(object) operation will leave the overloaded
					// principal in place (will not add the prev loaded principal)
					Iterator<Principal> it = tempPrincipals.iterator();
					while (it.hasNext())
					{
						Principal p = it.next();
						principals.add(p);
					}
				}
				else
				{
					// just add all the principals undecorated
					principals.addAll(tempPrincipals);
				}

				// add the credentials to the subject
				subject.getPublicCredentials().addAll(tempCredentials);

				tempPrincipals.clear();
				tempCredentials.clear();

				if (callbackHandler instanceof AccessCallbackHandler)
					((AccessCallbackHandler) callbackHandler).clearPassword();

				// in any case, clean out state
				cleanState();
				commitSucceeded = true;

				return true;
			}
			catch (Exception ex)
			{
				log.error(ex.getMessage(), ex);
				LoginException loginException = new LoginException(ex.getMessage());
				loginException.initCause(ex);
				throw loginException;
			}
		}
		else
		{
			return false;
		}
	}

	/**
	 * @see javax.security.auth.spi.LoginModule#abort()
	 */
	@Override
	public boolean abort()
	{
		log.debug("abort for {}", subject);

		// Clean out state
		succeeded = false;

		tempPrincipals.clear();
		tempCredentials.clear();

		if (callbackHandler instanceof AccessCallbackHandler)
			((AccessCallbackHandler) callbackHandler).clearPassword();

		logout();

		return true;
	}

	@Override
	public boolean logout()
	{
		log.debug("logout for {}", subject);

		tempPrincipals.clear();
		tempCredentials.clear();

		if (callbackHandler instanceof AccessCallbackHandler)
			((AccessCallbackHandler) callbackHandler).clearPassword();

		// remove the principals the login module added
		for (UserPrincipal p : subject.getPrincipals(UserPrincipal.class))
		{
			log.debug("removing UserPrincipal {}", p);
			subject.getPrincipals().remove(p);
		}
		for (RolePrincipal p : subject.getPrincipals(RolePrincipal.class))
		{
			log.debug("removing RolePrincipal {}", p);
			subject.getPrincipals().remove(p);
		}

		return true;
	}

	/**
	 * Get the username and password. This method does not return any value. Instead, it
	 * sets global name and password variables.
	 * <p>
	 * Also note that this method will set the username and password values in the shared
	 * state in case subsequent LoginModules want to use them via use/tryFirstPass.
	 * 
	 * @param getPasswdFromSharedState
	 *            boolean that tells this method whether to retrieve the password from the
	 *            sharedState.
	 */
	protected void getUsernamePassword(boolean getPasswdFromSharedState)
	{
		if (getPasswdFromSharedState)
		{
			// use the password saved by the first module in the stack
			username = (String) sharedState.get(NAME);
			password = (char[]) sharedState.get(PWD);
		}
	}

	/**
	 * Clean out state because of a failed authentication attempt
	 */
	protected void cleanState()
	{
		username = null;
		if (password != null)
		{
			for (int i = 0; i < password.length; i++)
				password[i] = ' ';
			password = null;
		}

		if (clearPass)
		{
			sharedState.remove(NAME);
			sharedState.remove(PWD);
		}
	}
}
