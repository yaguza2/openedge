package nl.openedge.access.impl.rdbms;

import java.util.*;

import javax.security.auth.spi.LoginModule;
import javax.security.auth.login.LoginException;
import javax.security.auth.Subject;
import javax.security.auth.callback.*;

import nl.openedge.access.impl.DefaultUser;

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
 * Based on an article in JavaWorld from Paul Feuer and John Musser
 *
 * @see     javax.security.auth.spi.LoginModule
 * @author  Eelco Hillenius
 */

public class RdbmsLoginModule extends RdbmsUserManager implements LoginModule {

    // initial state
    CallbackHandler callbackHandler;
    Subject  subject;
    Map      sharedState;
    Map      options;

    // temporary state
    Vector   tempCredentials;
    Vector   tempPrincipals;

    // the authentication status
    boolean  success;
    
	/** logger */
	private Log log = LogFactory.getLog(this.getClass());
   

    /**
     * <p>Creates a login module that can authenticate against
     * a JDBC datasource.
     */
    public RdbmsLoginModule() {
        
        tempCredentials = new Vector();
        tempPrincipals  = new Vector();
        success = false;
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
    }

    /**
     * <p> Verify the password against the relevant JDBC datasource.
     *
     * @return true always, since this <code>LoginModule</code>
     *      should not be ignored.
     *
     * @exception FailedLoginException if the authentication fails. <p>
     *
     * @exception LoginException if this <code>LoginModule</code>
     *      is unable to perform the authentication.
     * 
     * @see javax.security.auth.spi.LoginModule#login()
     */
    public boolean login() throws LoginException {

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

            String username = ((NameCallback)callbacks[0]).getName();
            String password = new String(((PasswordCallback)callbacks[1]).getPassword());

            ((PasswordCallback)callbacks[1]).clearPassword();

            success = rdbmsValidate(username, password);

            callbacks[0] = null;
            callbacks[1] = null;

            if (!success)
                throw new LoginException("Authentication failed: Invallid combination of username and password");

            return(true);
        } catch (LoginException ex) {
            throw ex;
        } catch (Exception ex) {
            success = false;
            throw new LoginException(ex.getMessage());
        }
    }

	/**
	 * @see javax.security.auth.spi.LoginModule#commit()
	 */
    public boolean commit() throws LoginException {

		if(log.isDebugEnabled()) log.debug("commit for " + subject);

        if (success) {

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

                return(true);
            } catch (Exception ex) {
                ex.printStackTrace(System.out);
                throw new LoginException(ex.getMessage());
            }
        } else {
            tempPrincipals.clear();
            tempCredentials.clear();
            return(true);
        }
    }

	/**
	 * @see javax.security.auth.spi.LoginModule#abort()
	 */
    public boolean abort() throws javax.security.auth.login.LoginException {

		if(log.isDebugEnabled()) log.debug("abort for " + subject);

        // Clean out state
        success = false;

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
        Iterator it = subject.getPrincipals(RdbmsPrincipal.class).iterator();
        while (it.hasNext()) {
            RdbmsPrincipal p = (RdbmsPrincipal)it.next();
            if(log.isDebugEnabled()) log.debug("removing principal " + p);
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
     * Validate the given user and password against the JDBC datasource.
     * <p>
     *
     * @param user the username to be authenticated. <p>
     * @param pass the password to be authenticated. <p>
     * @exception Exception if the validation fails.
     */
    private boolean rdbmsValidate(String username, String password) throws Exception {
        
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
            throw new LoginException("User " + username + " not found");

        if(log.isDebugEnabled()) log.debug("'" + password + "' equals '" + dbPassword + "'?");

        passwordMatch = password.equals(dbPassword);
        if (passwordMatch) {
			if(log.isDebugEnabled()) log.debug("passwords do match!");
            
			RdbmsCredential rdbmsCredential = new RdbmsCredential();
            this.tempCredentials.add(rdbmsCredential);
    
    		// construct user        
            DefaultUser user = new DefaultUser();
            user.setName(dbName);
			// add attributes
			addAttributes(user);
			// add groups
			addGroups(user);
			
            this.tempCredentials.add(user);
         
        } else {
            if(log.isDebugEnabled()) log.debug("passwords do NOT match!");
        }

        return passwordMatch;
    }
}

