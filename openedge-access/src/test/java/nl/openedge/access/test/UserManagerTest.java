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
package nl.openedge.access.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nl.openedge.access.RolePrincipal;
import nl.openedge.access.UserPrincipal;
import nl.openedge.access.UserManager;

/**
 * Testing of <code>UserManager</code> implementations
 * 
 * @author E.F. Hillenius
 */
public class UserManagerTest extends AbstractTestBase {

	protected String username = "_testuser";
	protected String rolename1 = "_testrole1";
	protected String rolename2 = "_testrole2";

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
			//assertNotSameContents("test roles and current roles should differ",
			//	roles, testRoles);

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
	
	/* get a user and change password */
	public void testGetUserAndResetPassword() {
		
		UserManager userManager = accessFactory.getUserManager();
		UserPrincipal user = null;
		try {
			user = userManager.getUser(username);
			assertNotNull(user);
			
			String anotherPassword = "anotherPassword";
			userManager.resetPassword(user, anotherPassword);
			
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
