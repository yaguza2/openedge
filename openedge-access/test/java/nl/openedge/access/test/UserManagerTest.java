/*
 * Created on 7-apr-2003
 */
package nl.openedge.access.test;

import nl.openedge.access.UserPrincipal;
import nl.openedge.access.UserManager;

/**
 * @author Hillenius
 * $Id$
 */
public class UserManagerTest extends AbstractTestBase {

	/**
	 * @param name
	 * @throws Exception
	 */
	public UserManagerTest(String name) throws Exception {
		super(name);
	}

	/** create a user */
	public void testCreateUser() {
		
		UserManager userManager = accessFactory.getUserManager();
		String username = "eelco";
		String password = "hillenius";
		UserPrincipal user = null;
		try {
			user = userManager.createUser(username, password);
			assertNotNull(user);
		} catch(Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	/* get a user */
	public void testGetUser() {
		
		UserManager userManager = accessFactory.getUserManager();
		String username = "eelco";
		UserPrincipal user = null;
		try {
			user = userManager.getUser(username);
			assertNotNull(user);
		} catch(Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	/* get a user */
	public void testDeleteUser() {
		
		UserManager userManager = accessFactory.getUserManager();
		String username = "eelco";
		UserPrincipal user = new UserPrincipal(username);
		try {
			userManager.deleteUser(user);
		} catch(Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
