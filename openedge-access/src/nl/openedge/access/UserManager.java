package nl.openedge.access;

import java.util.List;
import java.util.Map;

/**
 * @author vries
 *
 * this manager manages the users and roles, it can create, and
 * remove users and roles, and add users to roles
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
