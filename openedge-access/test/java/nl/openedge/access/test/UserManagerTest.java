/*
 * Created on 7-apr-2003
 */
package nl.openedge.access.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nl.openedge.access.GroupPrincipal;
import nl.openedge.access.UserPrincipal;
import nl.openedge.access.UserManager;

/**
 * @author Hillenius
 * $Id$
 */
public class UserManagerTest extends AbstractTestBase {

	protected String username = "eelco";
	protected String groupname1 = "testgroup1";
	protected String groupname2 = "testgroup2";

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
		List groups = null;
		try {
			groups = userManager.listUsers();
			assertNotNull("groups should be created", groups);
			assertFalse("groups should be not empty", groups.isEmpty());
		} catch(Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	/** create a group */
	public void testCreateGroup() {
		
		UserManager userManager = accessFactory.getUserManager();
		GroupPrincipal group1 = null;
		GroupPrincipal group2 = null;
		try {
			group1 = userManager.createGroup(groupname1);
			assertNotNull("group1 should be created", group1);
			group2 = userManager.createGroup(groupname2);
			assertNotNull("group1 should be created", group2);
		} catch(Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	/** get a group */
	public void testGetGroup() {
		
		UserManager userManager = accessFactory.getUserManager();
		GroupPrincipal group1 = null;
		try {
			group1 = userManager.getGroup(groupname1);
			assertNotNull("group1 should exist", group1);
		} catch(Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	/** list groups */
	public void testListGroups() {
		
		UserManager userManager = accessFactory.getUserManager();
		List groups = null;
		try {
			groups = userManager.listGroups();
			assertNotNull("groups should exist", groups);
			assertFalse("groups should exist", groups.isEmpty());
		} catch(Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	/** add a user to a group */
	public void testUserGroupMaps() {
		
		UserManager userManager = accessFactory.getUserManager();
		UserPrincipal user = new UserPrincipal(username);
		GroupPrincipal group1 = new GroupPrincipal(groupname1);
		GroupPrincipal group2 = new GroupPrincipal(groupname2);
		try {
			userManager.addUserToGroup(user, group1);
			userManager.addUserToGroup(user, group2);			
			List testGroups = new ArrayList(2);
			testGroups.add(group1);
			testGroups.add(group2);
			
			List groups = userManager.listGroupsForUser(user);			
			assertSameContents("test groups and current groups should be same",
				groups, testGroups);		
			userManager.removeUserFromGroup(user, group2);

			groups = userManager.listGroupsForUser(user);
			assertNotSameContents("test groups and current groups should differ",
				groups, testGroups);

			testGroups.remove(group2);
			assertSameContents("test groups and current groups should be same",
				groups, testGroups);			
			
			userManager.deleteGroup(group2);
			
			List testUsers = new ArrayList(1);
			testUsers.add(user);
			userManager.listUsersInGroup(group1);
			assertSameContents("test users and current users should be same",
				groups, testGroups);
			
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
			
			List groups = userManager.listGroupsForUser(dummy);
			assertTrue(groups.isEmpty());
			
		} catch(Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/* get a user */
	public void testDeleteGroup() {
		
		UserManager userManager = accessFactory.getUserManager();
		GroupPrincipal group = new GroupPrincipal(groupname1);
		try {
			userManager.deleteGroup(group);
			
			group = userManager.getGroup(groupname1);
			assertNull("group should be non existent", group);
			
		} catch(Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
