/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package nl.openedge.access;

import java.util.List;
import java.util.Map;

/**
 * The UserManager manages users and roles.
 * 
 * @author A.J. de Vries
 * @author E.F. Hillenius
 */
public interface UserManager extends Configurable {
	
	/**
	 * creates a new UserPrincipal object
	 * @param id
	 * @param name
	 * @param password
	 * @return UserPrincipal
	 */
	public UserPrincipal createUser(String name, String password) throws AccessException;
	
	/**
	 * set password to newPassword
	 * @param user
	 * @param newPassword
	 * @throws AccessException
	 */
	public void resetPassword(UserPrincipal user, String newPassword) throws AccessException;
	
	/**
	 * creates a new RolePrincipal object
	 * @param id
	 * @param name
	 * @return RolePrincipal
	 */
	public RolePrincipal createRole(String name) throws AccessException;
	
	/**
	 * returns a user with this name
	 * @param name
	 * @return UserPrincipal
	 */
	public UserPrincipal getUser(String name) throws AccessException;
	
	/**
	 * get attibutes for user
	 * @param user
	 * @return Map
	 * @throws AccessException
	 */
	public Map getUserAttributes(UserPrincipal user) throws AccessException;

	/**
	 * get a user attribute
	 * @param user
	 * @param key
	 * @return Object
	 * @throws AccessException
	 */
	public Object getUserAttribute(UserPrincipal user, String key) throws AccessException;
	
	/**
	 * change or add a user attribute
	 * @param user
	 * @param key
	 * @param value
	 * @throws AccessException
	 */
	public void setUserAttibute(UserPrincipal user, String key, Object value)
		throws AccessException;
		
	/**
	 * delete a attribute for user
	 * @param user
	 * @param key
	 * @throws AccessException
	 */
	public void removeUserAttribute(UserPrincipal user, String key) throws AccessException;
	
	/**
	 * remove all attibutes for user
	 * @param user
	 * @throws AccessException
	 */
	public void removeUserAttribute(UserPrincipal user) throws AccessException;
	
	/**
	 * returns a role with this id
	 * @param id
	 * @return RolePrincipal
	 */	
	public RolePrincipal getRole(String name) throws AccessException;
	
	/**
	 * list all current users
	 * @return List
	 */
	public List listUsers() throws AccessException;
	
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
	public List listUsersInRole(RolePrincipal role) throws AccessException;
	
	/**
	 * list all roles this user belongs to
	 * @param user
	 * @return List
	 */
	public List listRolesForUser(UserPrincipal user) throws AccessException;
	
	/**
	 * checks if user is in role
	 * @param user
	 * @param role
	 * @return boolean
	 */
	public boolean isUserInRole(UserPrincipal user, RolePrincipal role) throws AccessException;
	
	/**
	 * adds user to role
	 * @param user
	 * @param role
	 */	
	public void addUserToRole(UserPrincipal user, RolePrincipal role) throws AccessException;
	
	/**
	 * removes user from role
	 * @param user
	 * @param role
	 */
	public void removeUserFromRole(UserPrincipal user, RolePrincipal role) throws AccessException;
	
	/**
	 * remove user from users
	 * @param user
	 */
	public void deleteUser(UserPrincipal user) throws AccessException;
	
	/**
	 * remove role from roles
	 * @param role
	 */
	public void deleteRole(RolePrincipal role) throws AccessException;
}
