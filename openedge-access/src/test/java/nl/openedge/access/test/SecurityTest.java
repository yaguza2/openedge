package nl.openedge.access.test;

import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;

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
	
	/** test permission that is available for everyone */
	public void testNamedPermission4All() {
		
		// test named permission that everybody SHOULD have permission for
		String uri = "/forall";
		UriAction action = new UriAction(uri);
		try {
			Subject.doAsPrivileged(null, action, null);
			// if we get here, the user was authorised
		} catch (SecurityException se) {
			// Subject does not have permission
			se.printStackTrace();
			fail(se.getMessage());
		}
	}
	
	/** test permission */
	public void testNamedPermission4Group() {
		
		// test named permission that subject with group SHOULD have permission for
		String uri = "/forgroup";
		UriAction action = new UriAction(uri);
		try {
			// Subject.doAs(subject, action) does NOT work... dunno why?
			// nevertheless, this does. 
			Subject.doAsPrivileged(subject, action, null);
			// if we get here, the user was authorised
		} catch (SecurityException se) {
			// Subject does not have permission
			se.printStackTrace();
			fail(se.getMessage());
		}
	}
	
	/** test permission */
	public void testNamedPermission4SameUser() {
		
		// test named permission that subject with user SHOULD have permission for
		String uri = "/foreelco";
		UriAction action = new UriAction(uri);
		try {
			Subject.doAsPrivileged(subject, action, null);
			// if we get here, the user was authorised
		} catch (SecurityException se) {
			// Subject does not have permission
			se.printStackTrace();
			fail(se.getMessage());
		}
	}
	
	/** test permission */
	public void testNamedPermission4OtherUser() {
		
		// test named permission that subject with user SHOULD NOT have permission for
		String uri = "/forsander";
		UriAction action = new UriAction(uri);
		try {
			Subject.doAsPrivileged(subject, action, null);
			// if we get here, the user was authorised which is NOT right
			fail("user " + subject + " got wrong permission!");
		} catch (SecurityException se) {
			// Subject does not have permission
			// which is perfectely right!
		}
	}
	
	/** test permission */
	public void testNamedPermission4Noone() {
		
		// test named permission that no-one have permission for
		// (as it is not in the policy file)
		String uri = "/notInConifig";
		UriAction action = new UriAction(uri);
		try {
			Subject.doAsPrivileged(subject, action, null);
			// if we get here, the user was authorised which is NOT right
			fail("user " + subject + " got wrong permission!");
		} catch (SecurityException se) {
			// Subject does not have permission
			// which is perfectely right!
		}
	}
	
	
//---------------------------- Utility ---------------------------------------
	/** action for checking permissions for a specific subject */
	class UriAction implements PrivilegedAction {
		
		// uri to check on
		private String uri;
		
		/** construct with uri */
		public UriAction(String uri) {
			this.uri = uri;
		}
		
		/** run check */
		public Object run() {
			Permission p = new NamedPermission(uri);
			AccessController.checkPermission(p);
			return null;
		}
	}

}
