package nl.openedge.access.test;

import java.security.AccessControlException;
import java.security.AccessController;
import java.security.Permission;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;

import nl.openedge.access.NamedPermission;
import nl.openedge.access.PassiveCallbackHandler;


/**
 * @author Hillenius
 * $Id$
 */
public class SecurityTest extends AbstractTestBase {

	/** username to use with tests */
	protected static String username = "Eelco";
	
	/** password to use with tests */
	protected static String password = "illy";
	
	/** login module name to use with tests */
	protected static String moduleAlias = "TestLoginModule";

	/** if first test succeded, save subject for later use */
	protected static Subject subject = null;

	/**
	 * construct with name
	 * @param name
	 */
	public SecurityTest(String name) throws Exception {
		super(name);
	}

	/** test a user login */
	public void testLogin() {
		
		try {
			// create the callback handler
			PassiveCallbackHandler cbh = 
				new PassiveCallbackHandler(username, password);
			// create the login context
			LoginContext lc = new LoginContext(moduleAlias, cbh);	
			lc.login();
			// if we get here, login succeded
			subject = lc.getSubject();
			assertNotNull(subject);
			
		} catch(Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	/** test permission */
	public void testNamedPermission1() {
		
		// test named permission that subject SHOULD have permission for
		String uri = "TODO"; // TODO: test url
		try {
			
			Permission p = new NamedPermission("/TODO");
			AccessController.checkPermission(p);
			// if we get here, the user was authorised
			
		} catch(AccessControlException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	/** test permission */
	public void testNamedPermission2() {
		
		// test named permission that subject SHOULD NOT have permission for
		String uri = "TODO"; // TODO: test url
		try {
			
			Permission p = new NamedPermission("/TODO");
			AccessController.checkPermission(p);
			// if we get here, the user was authorised, which is not valid!
			fail(subject + " got permission for guarded domain!");
			
		} catch(AccessControlException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
