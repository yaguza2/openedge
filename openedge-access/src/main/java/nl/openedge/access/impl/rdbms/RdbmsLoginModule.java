/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package nl.openedge.access.impl.rdbms;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * RdbmsLoginModule is a LoginModule that authenticates
 * a given username/password credential against a JDBC
 * datasource.
 *
 * <p> This <code>LoginModule</code> interoperates with
 * any conformant JDBC datasource.
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

public class RdbmsLoginModule extends RdbmsUserManager 
		implements LoginModule {

    // initial state
	private CallbackHandler callbackHandler;
	private Subject subject;
	private Map sharedState;
	private Map options;
    
	private boolean useFirstPass = false;
	private boolean tryFirstPass = false;
	private boolean storePass = false;
	private boolean clearPass = false;
	
	private String username;
	private char[] password;
	
	// the authentication status
	private boolean succeeded = false;
	private boolean commitSucceeded = false;
	
	public static final String NAME = "javax.security.auth.login.name";
	public static final String PWD = "javax.security.auth.login.password";

    // temporary state
    protected Vector tempCredentials;
    protected Vector tempPrincipals;
    
    // decorator if provided
    protected LoginDecorator decorator = null;

    
	/** logger */
	private Log log = LogFactory.getLog(this.getClass());
   

    /**
     * <p>Creates a login module that can authenticate against
     * a JDBC datasource.
     */
    public RdbmsLoginModule() {
        
        tempCredentials = new Vector();
        tempPrincipals  = new Vector();
    }

	/**
	 * @see javax.security.auth.spi.LoginModule#initialize(javax.security.auth.Subject, javax.security.auth.callback.CallbackHandler, java.util.Map, java.util.Map)
	 */
    public void initialize(Subject subject, CallbackHandler callbackHandler,
            Map sharedState, Map options) {

        // save the initial state
        this.callbackHandler = callbackHandler;
        this.subject = subject;
        this.sharedState = sharedState;
        this.options = options;
        
        // read options
		tryFirstPass =
			"true".equalsIgnoreCase((String)options.get("tryFirstPass"));
		useFirstPass =
			"true".equalsIgnoreCase((String)options.get("useFirstPass"));
		storePass =
			"true".equalsIgnoreCase((String)options.get("storePass"));
		clearPass =
			"true".equalsIgnoreCase((String)options.get("clearPass"));
			
		// see if there's a LoginDecorater configured
		String decoClass = (String)options.get("decorator");
		if(decoClass != null) {
			// got one, try to instantiate
			try {				
				Class cls = Thread.currentThread()
								  .getContextClassLoader()
								  .loadClass(decoClass);
				this.decorator = (LoginDecorator)cls.newInstance();
			} catch(Exception e) {
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
	public boolean login() throws LoginException {
	
		// attempt the authentication
		if(tryFirstPass) {
	
			try {
				// attempt the authentication by getting the
				// username and password from shared state
				attemptAuthentication(true);
		
				// authentication succeeded
				succeeded = true;
				if(log.isDebugEnabled())log.debug("tryFirstPass succeeded");
				return true;
			} catch(LoginException le) {
				// authentication failed -- try again below by prompting
				cleanState();
				if(log.isDebugEnabled()) log.debug("tryFirstPass failed with:" +
						le.toString());
			}
	
		} else if(useFirstPass) {
	
			try {
				// attempt the authentication by getting the
				// username and password from shared state
				attemptAuthentication(true);
		
				// authentication succeeded
				succeeded = true;
				if(log.isDebugEnabled()) log.debug("useFirstPass succeeded");
				return true;
			} catch(LoginException le) {
				// authentication failed
				cleanState();
				if(log.isDebugEnabled()) log.debug("useFirstPass failed");
				throw le;
			}
		}
	
		// attempt the authentication by prompting for the username and pwd
		try {
			attemptAuthentication(false);
	
			// authentication succeeded
		   	succeeded = true;
			if(log.isDebugEnabled()) log.debug("regular authentication succeeded");
			return true;
		} catch(LoginException le) {
			cleanState();
			if(log.isDebugEnabled()) log.debug("regular authentication failed");
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
    public void attemptAuthentication(boolean getPasswdFromSharedState) throws LoginException {

		if(log.isDebugEnabled()) log.debug("login for " + subject);

        if(callbackHandler == null)
            throw new LoginException("Error: no CallbackHandler available " +
                    "to gather authentication information from the user");

        try {
            // Setup default callback handlers.
            Callback[] callbacks = new Callback[] {
                new NameCallback("Username: "),
                new PasswordCallback("Password: ", false)
            };

            callbackHandler.handle(callbacks);

            username = ((NameCallback)callbacks[0]).getName();
            password = ((PasswordCallback)callbacks[1]).getPassword();

            ((PasswordCallback)callbacks[1]).clearPassword();

            succeeded = rdbmsValidate(username, password);

            callbacks[0] = null;
            callbacks[1] = null;

            if(!succeeded)
                throw new LoginException("Authentication failed: Invallid combination of username and password");

			// save input as shared state only if
			// authentication succeeded
			if(storePass &&
				!sharedState.containsKey(NAME) &&
				!sharedState.containsKey(PWD)) {
				sharedState.put(NAME, username);
				sharedState.put(PWD, password);
			}

			succeeded = true;
        } catch (LoginException ex) {
            throw ex;
        } catch (Exception ex) {
			succeeded = false;
			ex.printStackTrace();
            throw new LoginException(ex.getMessage());
        }
    }
    
	/**
	 * Validate the given user and password against the JDBC datasource.
	 * <p>
	 *
	 * @param user the username to be authenticated. <p>
	 * @param pass the password to be authenticated. <p>
	 * @exception Exception if the validation fails.
	 */
	private boolean rdbmsValidate(String username, char[] password) throws Exception {
        
		boolean passwordMatch = false;
		String dbPassword = null;
		String dbName = null;
		boolean isEqual = false;

		Object[] userParams = new Object[]{ username };
		QueryResult result = excecuteQuery(
			queries.getProperty("selectUserStmt"), userParams);
		
		if(result.getRowCount() == 0) {
			throw new LoginException("User " + username + " not found");
		} else if(result.getRowCount() > 1) {
			throw new LoginException("Ambiguous user (located more than once): " + username);	
		}
		
		Map row = result.getRows()[0];
		dbName = (String)row.get("name");
		dbPassword = (String)row.get("password");

		if(dbPassword == null)
			throw new LoginException("UserPrincipal " + username + " not found");

		String cryptedPassword = new String(
					PasswordHelper.cryptPassword(password));

		passwordMatch = new String(cryptedPassword).equals(dbPassword);
		if(passwordMatch) {
            
			RdbmsCredential rdbmsCredential = new RdbmsCredential();
			this.tempCredentials.add(rdbmsCredential);
    
			// construct user        
			UserPrincipal user = new UserPrincipal(dbName);
			// add attributes
			addAttributes(user);
			// add roles
			this.tempPrincipals.add(user);
			
			List roles = listRolesForUser(user);
			user.setRoles(roles);
			this.tempPrincipals.addAll(roles);
         
		} else {
			//passwords do NOT match!
		}

		return passwordMatch;
	}

	/**
	 * @see javax.security.auth.spi.LoginModule#commit()
	 */
    public boolean commit() throws LoginException {

		if(log.isDebugEnabled()) log.debug("commit for " + subject + 
				" (succeeded == " + succeeded + ")");

        if(succeeded) {

            if(subject.isReadOnly()) throw new LoginException("Subject is Readonly");
            
            try {
            	
				Set principals = subject.getPrincipals();
                if(decorator != null) { // decorate if a decorator was set 
	                Iterator it = tempPrincipals.iterator();
	                while(it.hasNext()) {
						
						Principal original = (Principal)it.next();
						Principal decorated = decorator.decorate(original);
						
						if(decorated != null) { 
							// store the decorated principal instead of the original
							principals.add(decorated);
							if(log.isDebugEnabled()) 
									log.debug("replaced principal " + original +
											  " with " + decorated);
						} else { // store the original principal
							principals.add(original);
						}
	                }
                } else {
                	// just add all the principals undecorated
					principals.addAll(tempPrincipals);	
                }

                // add the credentials to the subject
                subject.getPublicCredentials().addAll(tempCredentials);

                tempPrincipals.clear();
                tempCredentials.clear();

                if(callbackHandler instanceof PassiveCallbackHandler)
                    ((PassiveCallbackHandler)callbackHandler).clearPassword();

				// in any case, clean out state
				cleanState();
				commitSucceeded = true;

                return true;
            } catch (Exception ex) {
                ex.printStackTrace(System.out);
                throw new LoginException(ex.getMessage());
            }
        } else {
            return false;
        }
    }

	/**
	 * @see javax.security.auth.spi.LoginModule#abort()
	 */
    public boolean abort() throws javax.security.auth.login.LoginException {

		if(log.isDebugEnabled()) log.debug("abort for " + subject);

        // Clean out state
		succeeded = false;

        tempPrincipals.clear();
        tempCredentials.clear();

        if (callbackHandler instanceof PassiveCallbackHandler)
            ((PassiveCallbackHandler)callbackHandler).clearPassword();

        logout();

        return(true);
    }

	/**
	 * @see javax.security.auth.spi.LoginModule#logout()
	 */
    public boolean logout() throws javax.security.auth.login.LoginException {

		if(log.isDebugEnabled()) log.debug("logout for " + subject);

        tempPrincipals.clear();
        tempCredentials.clear();

        if (callbackHandler instanceof PassiveCallbackHandler)
            ((PassiveCallbackHandler)callbackHandler).clearPassword();

        // remove the principals the login module added
        Iterator it = subject.getPrincipals(UserPrincipal.class).iterator();
        while (it.hasNext()) {
			UserPrincipal p = (UserPrincipal)it.next();
            if(log.isDebugEnabled()) log.debug("removing UserPrincipal " + p);
            subject.getPrincipals().remove(p);
        }
		it = subject.getPrincipals(RolePrincipal.class).iterator();
				while (it.hasNext()) {
					RolePrincipal p = (RolePrincipal)it.next();
					if(log.isDebugEnabled()) log.debug("removing RolePrincipal " + p);
					subject.getPrincipals().remove(p);
				}

        // remove the credentials the login module added
        it = subject.getPublicCredentials(RdbmsCredential.class).iterator();
        while (it.hasNext()) {
            RdbmsCredential c = (RdbmsCredential)it.next();
            if(log.isDebugEnabled()) log.debug("removing Credential " + c);
            subject.getPrincipals().remove(c);
        }

        return(true);
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
	private void getUsernamePassword(boolean getPasswdFromSharedState)
				throws LoginException {

		if (getPasswdFromSharedState) {
			// use the password saved by the first module in the stack
			username = (String)sharedState.get(NAME);
			password = (char[])sharedState.get(PWD);
			return;
		}
	}
    
	/**
	 * Clean out state because of a failed authentication attempt
	 */
	private void cleanState() {
		username = null;
		if (password != null) {
			for (int i = 0; i < password.length; i++)
			password[i] = ' ';
			password = null;
		}
	
		if (clearPass) {
			sharedState.remove(NAME);
			sharedState.remove(PWD);
		}
	}
	
}

