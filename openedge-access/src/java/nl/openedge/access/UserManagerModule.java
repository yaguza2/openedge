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
package nl.openedge.access;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The UserManagerModule manages users and roles.
 * 
 * @author A.J. de Vries
 * @author E.F. Hillenius
 */
public interface UserManagerModule
{

	//--------------------------- USERS ----------------------------------//

	/**
	 * creates a new Principal object
	 * @param id
	 * @param name
	 * @param password
	 * @param attributes like first-name etc.
	 * @return Principal
	 */
	public Principal createUser(String name, String password, Map attributes) 
			throws AccessException;

	/**
	 * returns a user with this name
	 * @param name
	 * @return Principal
	 */
	public Principal getUser(String name) throws AccessException;

	/**
	 * list all current users
	 * @return List
	 */
	public List listUsers() throws AccessException;

	/**
	 * set password to newPassword
	 * @param user
	 * @param newPassword
	 * @throws AccessException
	 */
	public void resetPassword(Principal user, String newPassword) 
			throws AccessException;

	/**
	 * remove user from users
	 * @param user
	 */
	public void deleteUser(Principal user) throws AccessException;

	//	--------------------------- ROLES ----------------------------------//

	/**
	 * creates a new Principal object
	 * @param id
	 * @param name
	 * @return Principal
	 */
	public Principal createRole(String name) throws AccessException;

	/**
	 * returns a role with this id
	 * @param id
	 * @return Principal
	 */
	public Principal getRole(String name) throws AccessException;

	/**
	 * list all current roles
	 * @return List
	 */
	public List listRoles() throws AccessException;

	/**
	 * list users from this role
	 * @param role
	 * @return List
	 */
	public List listUsersInRole(Principal role) throws AccessException;

	/**
	 * list all roles this user belongs to
	 * @param user
	 * @return List
	 */
	public Set listRolesForUser(Principal user) throws AccessException;

	/**
	 * adds user to role
	 * @param user
	 * @param role
	 */
	public void addUserToRole(Principal user, Principal role) 
			throws AccessException;

	/**
	 * removes user from role
	 * @param user
	 * @param role
	 */
	public void removeUserFromRole(Principal user, Principal role) 
			throws AccessException;

	/**
	 * remove role from roles
	 * @param role
	 */
	public void deleteRole(Principal role) throws AccessException;

	//	--------------------------- GROUPS ----------------------------------//

	/**
	 * creates a new Principal object
	 * @param id
	 * @param name
	 * @return Principal
	 */
	public Principal createGroup(String name) throws AccessException;

	/**
	 * returns a group with this id
	 * @param id
	 * @return Principal
	 */
	public Principal getGroup(String name) throws AccessException;

	/**
	 * list all current groups
	 * @return List
	 */
	public List listGroups() throws AccessException;

	/**
	 * list users from this group
	 * @param group
	 * @return List
	 */
	public List listUsersInGroup(Principal group) throws AccessException;

	/**
	 * list all groups this user belongs to
	 * @param user
	 * @return List
	 */
	public Set listGroupsForUser(Principal user) throws AccessException;

	/**
	 * adds user to group
	 * @param user
	 * @param group
	 */
	public void addUserToGroup(Principal user, Principal group) 
			throws AccessException;

	/**
	 * removes user from group
	 * @param user
	 * @param group
	 */
	public void removeUserFromGroup(Principal user, Principal group) 
			throws AccessException;

	/**
	 * remove group from groups
	 * @param group
	 */
	public void deleteGroup(Principal group) throws AccessException;

}
