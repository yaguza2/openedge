package nl.openedge.access.impl.rdbms;

import java.util.*;

import javax.security.auth.spi.LoginModule;
import javax.security.auth.login.LoginException;
import javax.security.auth.Subject;
import javax.security.auth.callback.*;

import nl.openedge.access.*;
import nl.openedge.access.RolePrincipal;
import nl.openedge.access.UserPrincipal;

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
	
	private static final String NAME = "javax.security.auth.login.name";
	private static final String PWD = "javax.security.auth.login.password";

    // temporary state
    Vector tempCredentials;
    Vector tempPrincipals;
    
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
		if (tryFirstPass) {
	
			try {
				// attempt the authentication by getting the
				// username and password from shared state
				attemptAuthentication(true);
		
				// authentication succeeded
				succeeded = true;
				if(log.isDebugEnabled())log.debug("tryFirstPass succeeded");
				return true;
			} catch (LoginException le) {
				// authentication failed -- try again below by prompting
				cleanState();
				if(log.isDebugEnabled()) log.debug("tryFirstPass failed with:" +
						le.toString());
			}
	
		} else if (useFirstPass) {
	
			try {
				// attempt the authentication by getting the
				// username and password from shared state
				attemptAuthentication(true);
		
				// authentication succeeded
				succeeded = true;
				if(log.isDebugEnabled()) log.debug("useFirstPass succeeded");
				return true;
			} catch (LoginException le) {
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
		} catch (LoginException le) {
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

        if (callbackHandler == null)
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

            if (!succeeded)
                throw new LoginException("Authentication failed: Invallid combination of username and password");

			// save input as shared state only if
			// authentication succeeded
			if (storePass &&
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

		if (dbPassword == null)
			throw new LoginException("UserPrincipal " + username + " not found");

		if(log.isDebugEnabled()) log.debug("'" + new String(password) + "' equals '" + dbPassword + "'?");

		passwordMatch = new String(password).equals(dbPassword);
		if (passwordMatch) {
			if(log.isDebugEnabled()) log.debug("passwords do match!");
            
			RdbmsCredential rdbmsCredential = new RdbmsCredential();
			this.tempCredentials.add(rdbmsCredential);
    
			// construct user        
			UserPrincipal user = new UserPrincipal(dbName);
			// add attributes
			addAttributes(user);
			// add roles
			this.tempCredentials.add(user);
			
			List roles = listRolesForUser(user);
			user.setRoles(roles);
			this.tempPrincipals.addAll(roles);
         
		} else {
			if(log.isDebugEnabled()) log.debug("passwords do NOT match!");
		}

		return passwordMatch;
	}

	/**
	 * @see javax.security.auth.spi.LoginModule#commit()
	 */
    public boolean commit() throws LoginException {

		if(log.isDebugEnabled()) log.debug("commit for " + subject);

        if (succeeded) {

            if (subject.isReadOnly()) {
                throw new LoginException ("Subject is Readonly");
            }

            try {
                Iterator it = tempPrincipals.iterator();
                
                if(log.isDebugEnabled()) {
                    while (it.hasNext())
                        log.debug("Principal: " + it.next().toString());
                }

                subject.getPrincipals().addAll(tempPrincipals);
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
					UserPrincipal p = (UserPrincipal)it.next();
					if(log.isDebugEnabled()) log.debug("removing UserPrincipal " + p);
					subject.getPrincipals().remove(p);
				}

        // remove the credentials the login module added
        it = subject.getPublicCredentials(RdbmsCredential.class).iterator();
        while (it.hasNext()) {
            RdbmsCredential c = (RdbmsCredential)it.next();
            if(log.isDebugEnabled()) log.debug("removing credential " + c);
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

