/*
 * $Id$
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
package nl.openedge.modules.impl.hibernate.usermanager.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.openedge.access.UserAttributeConstants;
import nl.openedge.access.UserManagerModule;
import nl.openedge.modules.impl.usermanager.hib.GroupPrincipal;
import nl.openedge.modules.impl.usermanager.hib.RolePrincipal;
import nl.openedge.modules.impl.usermanager.hib.UserPrincipal;

/**
 * Testing of <code>UserManagerModule</code> implementations
 * 
 * @author E.F. Hillenius
 */
public class UserManagerTest extends AbstractTestBase
{

	protected String username = "_testuser";
	protected String rolename1 = "_testrole1";
	protected String rolename2 = "_testrole2";
	protected String groupname1 = "_testgroup1";
	protected String groupname2 = "_testgroup2";

	/**
	 * @param name
	 * @throws Exception
	 */
	public UserManagerTest(String name) throws Exception
	{
		super(name);
	}

	/** create a user */
	public void testCreateUser()
	{

		UserManagerModule userManager = null;
		try
		{
			userManager = (UserManagerModule)
				moduleFactory.getComponent("UserManager");
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
		String password = "hillenius";
		Map attribs = new HashMap();
		attribs.put(UserAttributeConstants.FIRST_NAME, "Eelco");
		attribs.put(UserAttributeConstants.LAST_NAME, "Hillenius");
		attribs.put(UserAttributeConstants.EMAIL, "eelco.hillenius@openedge.nl");
		UserPrincipal user = null;
		try
		{
			user = (UserPrincipal)
				userManager.createUser(username, password, attribs);
			assertNotNull("user should be created", user);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/** list users */
	public void testListUsers()
	{

		UserManagerModule userManager = null;
		try
		{
			userManager = (UserManagerModule)
				moduleFactory.getComponent("UserManager");
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
		List roles = null;
		try
		{
			roles = userManager.listUsers();
			assertNotNull("roles should be created", roles);
			assertFalse("roles should be not empty", roles.isEmpty());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/** create a role */
	public void testCreateRole()
	{

		UserManagerModule userManager = null;
		try
		{
			userManager = (UserManagerModule)
				moduleFactory.getComponent("UserManager");
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
		RolePrincipal role1 = null;
		RolePrincipal role2 = null;
		try
		{
			role1 = (RolePrincipal)userManager.createRole(rolename1);
			assertNotNull("role1 should have been created", role1);
			role2 = (RolePrincipal)userManager.createRole(rolename2);
			assertNotNull("role2 should have been created", role2);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/** create a group */
	public void testCreateGroup()
	{

		UserManagerModule userManager = null;
		try
		{
			userManager = (UserManagerModule)
				moduleFactory.getComponent("UserManager");
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
		GroupPrincipal group1 = null;
		GroupPrincipal group2 = null;
		try
		{
			group1 = (GroupPrincipal)userManager.createGroup(groupname1);
			assertNotNull("group1 should have been created", group1);
			group2 = (GroupPrincipal)userManager.createGroup(groupname2);
			assertNotNull("group2 should have been created", group2);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/** get a role */
	public void testGetRole()
	{

		UserManagerModule userManager = null;
		try
		{
			userManager = (UserManagerModule)
				moduleFactory.getComponent("UserManager");
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
		RolePrincipal role1 = null;
		try
		{
			role1 = (RolePrincipal)userManager.getRole(rolename1);
			assertNotNull("role1 should exist", role1);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/** get a group */
	public void testGetGroup()
	{

		UserManagerModule userManager = null;
		try
		{
			userManager = (UserManagerModule)
				moduleFactory.getComponent("UserManager");
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
		GroupPrincipal group1 = null;
		try
		{
			group1 = (GroupPrincipal)userManager.getGroup(groupname1);
			assertNotNull("group1 should exist", group1);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/** list roles */
	public void testListRoles()
	{

		UserManagerModule userManager = null;
		try
		{
			userManager = (UserManagerModule)
				moduleFactory.getComponent("UserManager");
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
		List roles = null;
		try
		{
			roles = userManager.listRoles();
			assertNotNull("roles should exist", roles);
			assertFalse("roles should exist", roles.isEmpty());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/** list groups */
	public void testListGroups()
	{

		UserManagerModule userManager = null;
		try
		{
			userManager = (UserManagerModule)
				moduleFactory.getComponent("UserManager");
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
		List groups = null;
		try
		{
			groups = userManager.listGroups();
			assertNotNull("groups should exist", groups);
			assertFalse("groups should exist", groups.isEmpty());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/** test role maps */
	public void testUserRoleMaps()
	{

		UserManagerModule userManager = null;
		try
		{
			userManager = (UserManagerModule)
				moduleFactory.getComponent("UserManager");
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
		UserPrincipal user = new UserPrincipal(username);
		RolePrincipal role1 = new RolePrincipal(rolename1);
		RolePrincipal role2 = new RolePrincipal(rolename2);
		try
		{
			userManager.addUserToRole(user, role1);
			userManager.addUserToRole(user, role2);
			List testRoles = new ArrayList(2);
			testRoles.add(role1);
			testRoles.add(role2);

			Set roles = userManager.listRolesForUser(user);
			assertSameContents("test roles and current roles should be same", 
				roles, testRoles);
			userManager.removeUserFromRole(user, role2);

			roles = userManager.listRolesForUser(user);

			testRoles.remove(role2);
			assertSameContents("test roles and current roles should be same", 
				roles, testRoles);

			userManager.deleteRole(role2);

		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/** test group maps */
	public void testUserGroupMaps()
	{

		UserManagerModule userManager = null;
		try
		{
			userManager = (UserManagerModule)
				moduleFactory.getComponent("UserManager");
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
		UserPrincipal user = new UserPrincipal(username);
		GroupPrincipal group1 = new GroupPrincipal(groupname1);
		GroupPrincipal group2 = new GroupPrincipal(groupname2);
		try
		{
			userManager.addUserToGroup(user, group1);
			userManager.addUserToGroup(user, group2);
			List testGroups = new ArrayList(2);
			testGroups.add(group1);
			testGroups.add(group2);

			Set groups = userManager.listGroupsForUser(user);
			assertSameContents("test groups and current groups should be same", 
				groups, testGroups);
			userManager.removeUserFromGroup(user, group2);

			groups = userManager.listGroupsForUser(user);

			testGroups.remove(group2);
			assertSameContents("test groups and current groups should be same", 
				groups, testGroups);

			userManager.deleteGroup(group2);

		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/* get a user and change password */
	public void testGetUserAndResetPassword()
	{

		UserManagerModule userManager = null;
		try
		{
			userManager = (UserManagerModule)
				moduleFactory.getComponent("UserManager");
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
		UserPrincipal user = null;
		try
		{
			user = (UserPrincipal)userManager.getUser(username);
			assertNotNull(user);

			String anotherPassword = "anotherPassword";
			userManager.resetPassword(user, anotherPassword);

		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/* get a user */
	public void testDeleteUser()
	{

		UserManagerModule userManager = null;
		try
		{
			userManager = (UserManagerModule)
				moduleFactory.getComponent("UserManager");
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
		UserPrincipal user = new UserPrincipal(username);
		try
		{
			userManager.deleteUser(user);

			user = (UserPrincipal)userManager.getUser(username);
			assertNull("user should be non existent", user);

		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/* delete roles */
	public void testDeleteRole()
	{

		UserManagerModule userManager = null;
		try
		{
			userManager = (UserManagerModule)
				moduleFactory.getComponent("UserManager");
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
		RolePrincipal role1 = new RolePrincipal(rolename1);
		try
		{
			userManager.deleteRole(role1);

			role1 = (RolePrincipal)userManager.getRole(rolename1);
			assertNull("role should be non existent", role1);

		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/* delete groups */
	public void testDeleteGroup()
	{

		UserManagerModule userManager = null;
		try
		{
			userManager = (UserManagerModule)
				moduleFactory.getComponent("UserManager");
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
		GroupPrincipal group1 = new GroupPrincipal(groupname1);
		try
		{
			userManager.deleteGroup(group1);

			group1 = (GroupPrincipal)userManager.getGroup(groupname1);
			assertNull("group should be non existent", group1);

		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
