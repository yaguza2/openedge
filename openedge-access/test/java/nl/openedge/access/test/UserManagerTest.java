package nl.openedge.access.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nl.openedge.access.RolePrincipal;
import nl.openedge.access.UserPrincipal;
import nl.openedge.access.UserManager;

/**
 * @author Hillenius
 * $Id$
 */
public class UserManagerTest extends AbstractTestBase {

	protected String username = "eelco";
	protected String rolename1 = "testrole1";
	protected String rolename2 = "testrole2";

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
		String password = "hillenius";
		UserPrincipal user = null;
		try {
			user = userManager.createUser(username, password);
			assertNotNull("user should be created", user);
		} catch(Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	/** list users */
	public void testListUsers() {
		
		UserManager userManager = accessFactory.getUserManager();
		List roles = null;
		try {
			roles = userManager.listUsers();
			assertNotNull("roles should be created", roles);
			assertFalse("roles should be not empty", roles.isEmpty());
		} catch(Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	/** create a role */
	public void testCreateRole() {
		
		UserManager userManager = accessFactory.getUserManager();
		RolePrincipal role1 = null;
		RolePrincipal role2 = null;
		try {
			role1 = userManager.createRole(rolename1);
			assertNotNull("role1 should be created", role1);
			role2 = userManager.createRole(rolename2);
			assertNotNull("role1 should be created", role2);
		} catch(Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	/** get a role */
	public void testGetRole() {
		
		UserManager userManager = accessFactory.getUserManager();
		RolePrincipal role1 = null;
		try {
			role1 = userManager.getRole(rolename1);
			assertNotNull("role1 should exist", role1);
		} catch(Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	/** list roles */
	public void testListRoles() {
		
		UserManager userManager = accessFactory.getUserManager();
		List roles = null;
		try {
			roles = userManager.listRoles();
			assertNotNull("roles should exist", roles);
			assertFalse("roles should exist", roles.isEmpty());
		} catch(Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	/** add a user to a role */
	public void testUserRoleMaps() {
		
		UserManager userManager = accessFactory.getUserManager();
		UserPrincipal user = new UserPrincipal(username);
		RolePrincipal role1 = new RolePrincipal(rolename1);
		RolePrincipal role2 = new RolePrincipal(rolename2);
		try {
			userManager.addUserToRole(user, role1);
			userManager.addUserToRole(user, role2);			
			List testRoles = new ArrayList(2);
			testRoles.add(role1);
			testRoles.add(role2);
			
			List roles = userManager.listRolesForUser(user);			
			assertSameContents("test roles and current roles should be same",
				roles, testRoles);		
			userManager.removeUserFromRole(user, role2);

			roles = userManager.listRolesForUser(user);
			assertNotSameContents("test roles and current roles should differ",
				roles, testRoles);

			testRoles.remove(role2);
			assertSameContents("test roles and current roles should be same",
				roles, testRoles);			
			
			userManager.deleteRole(role2);
			
			List testUsers = new ArrayList(1);
			testUsers.add(user);
			userManager.listUsersInRole(role1);
			assertSameContents("test users and current users should be same",
				roles, testRoles);
			
		} catch(Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	/* get a user */
	public void testGetUser() {
		
		UserManager userManager = accessFactory.getUserManager();
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
		UserPrincipal user = new UserPrincipal(username);
		try {
			userManager.deleteUser(user);
			
			user = userManager.getUser(username);
			assertNull("user should be non existent", user);
			
			UserPrincipal dummy = new UserPrincipal(username);
			Map attribs = userManager.getUserAttributes(dummy);
			assertTrue(attribs.values().isEmpty());
			
			List roles = userManager.listRolesForUser(dummy);
			assertTrue(roles.isEmpty());
			
		} catch(Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/* get a user */
	public void testDeleteRole() {
		
		UserManager userManager = accessFactory.getUserManager();
		RolePrincipal role = new RolePrincipal(rolename1);
		try {
			userManager.deleteRole(role);
			
			role = userManager.getRole(rolename1);
			assertNull("role should be non existent", role);
			
		} catch(Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
