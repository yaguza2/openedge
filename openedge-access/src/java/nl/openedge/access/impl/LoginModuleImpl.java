/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Copyright (c) 2003, Open Edge B.V.
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, 
 * this list of conditions and the following disclaimer. Redistributions 
 * in binary form must reproduce the above copyright notice, this list of 
 * conditions and the following disclaimer in the documentation and/or other 
 * materials provided with the distribution. Neither the name of OpenEdge B.V. 
 * nor the names of its contributors may be used to endorse or promote products 
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package nl.openedge.access.impl;

import java.security.Principal;
import java.util.*;

import javax.security.auth.spi.LoginModule;
import javax.security.auth.login.LoginException;
import javax.security.auth.Subject;
import javax.security.auth.callback.*;

import nl.openedge.access.*;
import nl.openedge.access.RolePrincipal;
import nl.openedge.access.UserPrincipal;
import nl.openedge.access.util.PasswordHelper;
import nl.openedge.modules.ComponentRepository;
import nl.openedge.modules.RepositoryFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * LoginModuleImpl is a LoginModule that authenticates
 * a given username/password credential using Hibernate.
 * 
 * If the user entered a valid username and password,
 * this <code>LoginModule</code> associates a
 * <code>UserPrincipal</code> and the relevant <code>RolePrincipals</code>
 * with the <code>Subject</code>.
 * 
 * <p> This LoginModule also recognizes the following <code>Configuration</code>
 * options:
 * <pre>
 *
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
 * @see     javax.security.auth.spi.LoginModule
 * @author  Eelco Hillenius
 */

public class LoginModuleImpl implements LoginModule
{

	// initial state
	protected CallbackHandler callbackHandler;
	protected Subject subject;
	protected Map sharedState;
	protected Map options;

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
	protected Vector tempCredentials;
	protected Vector tempPrincipals;

	/** decorator if provided */
	protected LoginDecorator decorator = null;

	/** user manager, MUST be provided */
	protected UserManagerModule userManager = null;

	/** module alias for user manager: userManagerAlias */
	public final static String USER_MANAGER_ALIAS = "userManagerAlias";

	/* logger */
	protected Log log = LogFactory.getLog(this.getClass());

	/**
	 * <p>Creates a login module.
	 */
	public LoginModuleImpl()
	{

		tempCredentials = new Vector();
		tempPrincipals = new Vector();
	}

	/**
	 * @see javax.security.auth.spi.LoginModule#initialize(javax.security.auth.Subject, javax.security.auth.callback.CallbackHandler, java.util.Map, java.util.Map)
	 */
	public void initialize(Subject subject, CallbackHandler callbackHandler, 
					Map sharedState, Map options)
	{

		// save the initial state
		this.callbackHandler = callbackHandler;
		this.subject = subject;
		this.sharedState = sharedState;
		this.options = options;

		String userManagerAlias = (String)options.get(USER_MANAGER_ALIAS);

		try
		{
			ComponentRepository mf = RepositoryFactory.getRepository();
			userManager = (UserManagerModule)mf.getComponent(userManagerAlias);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return;
		}

		// read options
		tryFirstPass = "true".equalsIgnoreCase((String)options.get("tryFirstPass"));
		useFirstPass = "true".equalsIgnoreCase((String)options.get("useFirstPass"));
		storePass = "true".equalsIgnoreCase((String)options.get("storePass"));
		clearPass = "true".equalsIgnoreCase((String)options.get("clearPass"));

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if (classLoader == null)
		{
			classLoader = LoginModuleImpl.class.getClassLoader();
		}
		// see if there's a LoginDecorater configured
		String decoClass = (String)options.get("decorator");
		if (decoClass != null)
		{
			// got one, try to instantiate
			try
			{
				Class cls = classLoader.loadClass(decoClass);
				this.decorator = (LoginDecorator)cls.newInstance();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

	}

	/**
	 * <p> Prompt for username and password.
	 * Verify the password against the relevant name service.
	 *
	 * <p>
	 *
	 * @return true always, since this <code>LoginModule</code>
	 *		should not be ignored.
	 *
	 * @exception FailedLoginException if the authentication fails. <p>
	 *
	 * @exception LoginException if this <code>LoginModule</code>
	 *		is unable to perform the authentication.
	 */
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
				if (log.isDebugEnabled())
					log.debug("tryFirstPass succeeded");
				return true;
			}
			catch (LoginException le)
			{
				// authentication failed -- try again below by prompting
				cleanState();
				if (log.isDebugEnabled())
					log.debug("tryFirstPass failed with:" + le.toString());
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
				if (log.isDebugEnabled())
					log.debug("useFirstPass succeeded");
				return true;
			}
			catch (LoginException le)
			{
				// authentication failed
				cleanState();
				if (log.isDebugEnabled())
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
			if (log.isDebugEnabled())
				log.debug("regular authentication succeeded");
			return true;
		}
		catch (LoginException le)
		{
			cleanState();
			if (log.isDebugEnabled())
				log.debug("regular authentication failed");
			throw le;
		}
	}

	/**
	 * Attempt authentication
	 *
	 * <p>
	 *
	 * @param getPasswdFromSharedState boolean that tells this method whether
	 *		to retrieve the password from the sharedState.
	 */
	public void attemptAuthentication(boolean getPasswdFromSharedState) 
					throws LoginException
	{

		if (log.isDebugEnabled())
			log.debug("login for " + subject);

		if (callbackHandler == null)
			throw new LoginException(
				"Error: no CallbackHandler available " + 
				"to gather authentication information from the user");

		try
		{
			// Setup default callback handlers.
			Callback[] callbacks =
				new Callback[] { new NameCallback("Username: "), 
								 new PasswordCallback("Password: ", 
								 false)};

			callbackHandler.handle(callbacks);

			username = ((NameCallback)callbacks[0]).getName();
			password = ((PasswordCallback)callbacks[1]).getPassword();

			((PasswordCallback)callbacks[1]).clearPassword();

			succeeded = validate(username, password);

			callbacks[0] = null;
			callbacks[1] = null;

			if (!succeeded)
				throw new LoginException(
					"Authentication failed: Invallid combination of username and password");

			// save input as shared state only if
			// authentication succeeded
			if (storePass && !sharedState.containsKey(NAME) 
				&& !sharedState.containsKey(PWD))
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
			ex.printStackTrace();
			throw new LoginException(ex.getMessage());
		}
	}

	/**
	 * Validate the given user and password.
	 * <p>
	 *
	 * @param user the username to be authenticated. <p>
	 * @param pass the password to be authenticated. <p>
	 * @exception Exception if the validation fails.
	 */
	protected boolean validate(String username, char[] password) throws Exception
	{

		boolean passwordMatch = false;
		String dbPassword = null;
		String dbName = null;
		boolean isEqual = false;

		UserPrincipal user = (UserPrincipal)userManager.getUser(username);

		if (user == null)
		{
			throw new LoginException("User " + username + " not found");
		}

		String cryptedPassword = new String(PasswordHelper.cryptPassword(password));

		passwordMatch = new String(cryptedPassword).equals(user.getPassword());
		if (passwordMatch)
		{

			this.tempPrincipals.add(user);
			Set roles = userManager.listRolesForUser(user);
			if (roles != null)
			{
				this.tempPrincipals.addAll(roles);
			}
			Set groups = userManager.listGroupsForUser(user);
			if (groups != null)
			{
				this.tempPrincipals.addAll(groups);
			}
		}
		else
		{
			//passwords do NOT match!
		}

		return passwordMatch;
	}

	/**
	 * @see javax.security.auth.spi.LoginModule#commit()
	 */
	public boolean commit() throws LoginException
	{

		if (log.isDebugEnabled())
			log.debug("commit for " + subject + " (succeeded == " + succeeded + ")");

		if (succeeded)
		{

			if (subject.isReadOnly())
				throw new LoginException("Subject is Readonly");

			try
			{

				Set principals = subject.getPrincipals();
				if (decorator != null)
				{ // decorate if a decorator was set 
					// this gives use the chance to overload principals
					// or maybe even add totally new ones
					Principal[] originals = (Principal[])
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
					Iterator it = tempPrincipals.iterator();
					while (it.hasNext())
					{
						Principal p = (Principal)it.next();
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
					 ((AccessCallbackHandler)callbackHandler).clearPassword();

				// in any case, clean out state
				cleanState();
				commitSucceeded = true;

				return true;
			}
			catch (Exception ex)
			{
				ex.printStackTrace(System.out);
				throw new LoginException(ex.getMessage());
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
	public boolean abort() throws javax.security.auth.login.LoginException
	{

		if (log.isDebugEnabled())
			log.debug("abort for " + subject);

		// Clean out state
		succeeded = false;

		tempPrincipals.clear();
		tempCredentials.clear();

		if (callbackHandler instanceof AccessCallbackHandler)
			 ((AccessCallbackHandler)callbackHandler).clearPassword();

		logout();

		return (true);
	}

	/**
	 * @see javax.security.auth.spi.LoginModule#logout()
	 */
	public boolean logout() throws javax.security.auth.login.LoginException
	{

		if (log.isDebugEnabled())
			log.debug("logout for " + subject);

		tempPrincipals.clear();
		tempCredentials.clear();

		if (callbackHandler instanceof AccessCallbackHandler)
			 ((AccessCallbackHandler)callbackHandler).clearPassword();

		// remove the principals the login module added
		Iterator it = subject.getPrincipals(UserPrincipal.class).iterator();
		while (it.hasNext())
		{
			UserPrincipal p = (UserPrincipal)it.next();
			if (log.isDebugEnabled())
				log.debug("removing UserPrincipal " + p);
			subject.getPrincipals().remove(p);
		}
		it = subject.getPrincipals(RolePrincipal.class).iterator();
		while (it.hasNext())
		{
			RolePrincipal p = (RolePrincipal)it.next();
			if (log.isDebugEnabled())
				log.debug("removing RolePrincipal " + p);
			subject.getPrincipals().remove(p);
		}

		return (true);
	}

	/**
	 * Get the username and password.
	 * This method does not return any value.
	 * Instead, it sets global name and password variables.
	 *
	 * <p> Also note that this method will set the username and password
	 * values in the shared state in case subsequent LoginModules
	 * want to use them via use/tryFirstPass.
	 *
	 * <p>
	 *
	 * @param getPasswdFromSharedState boolean that tells this method whether
	 *		to retrieve the password from the sharedState.
	 */
	protected void getUsernamePassword(boolean getPasswdFromSharedState) 
						throws LoginException
	{

		if (getPasswdFromSharedState)
		{
			// use the password saved by the first module in the stack
			username = (String)sharedState.get(NAME);
			password = (char[])sharedState.get(PWD);
			return;
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
