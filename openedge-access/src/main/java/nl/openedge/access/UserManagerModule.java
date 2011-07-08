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
	/**
	 * creates a new Principal object
	 * 
	 * @param name
	 * @param password
	 * @param attributes
	 *            like first-name etc.
	 * @return Principal
	 */
	public UserPrincipal createUser(String name, String password, Map<String, Object> attributes)
			throws AccessException;

	/**
	 * returns a user with this name
	 * 
	 * @param name
	 * @return Principal
	 */
	public UserPrincipal getUser(String name) throws AccessException;

	/**
	 * list all current users
	 * 
	 * @return List
	 */
	public List<UserPrincipal> listUsers() throws AccessException;

	/**
	 * set password to newPassword
	 * 
	 * @param user
	 * @param newPassword
	 * @throws AccessException
	 */
	public void resetPassword(Principal user, String newPassword) throws AccessException;

	/**
	 * remove user from users
	 * 
	 * @param user
	 */
	public void deleteUser(Principal user) throws AccessException;

	/**
	 * creates a new Principal object
	 * 
	 * @param name
	 * @return Principal
	 */
	public RolePrincipal createRole(String name) throws AccessException;

	/**
	 * returns a role with this name
	 * 
	 * @param name
	 * @return Principal
	 */
	public RolePrincipal getRole(String name) throws AccessException;

	/**
	 * list all current roles
	 * 
	 * @return List
	 */
	public List<RolePrincipal> listRoles() throws AccessException;

	/**
	 * list users from this role
	 * 
	 * @param role
	 * @return List
	 */
	public List<RolePrincipal> listUsersInRole(Principal role) throws AccessException;

	/**
	 * list all roles this user belongs to
	 * 
	 * @param user
	 * @return List
	 */
	public Set<RolePrincipal> listRolesForUser(Principal user) throws AccessException;

	/**
	 * adds user to role
	 * 
	 * @param user
	 * @param role
	 */
	public void addUserToRole(Principal user, Principal role) throws AccessException;

	/**
	 * removes user from role
	 * 
	 * @param user
	 * @param role
	 */
	public void removeUserFromRole(Principal user, Principal role) throws AccessException;

	/**
	 * remove role from roles
	 * 
	 * @param role
	 */
	public void deleteRole(Principal role) throws AccessException;

	/**
	 * creates a new Principal object
	 * 
	 * @return Principal
	 */
	public GroupPrincipal createGroup(String name) throws AccessException;

	/**
	 * returns a group with this name
	 */
	public GroupPrincipal getGroup(String name) throws AccessException;

	/**
	 * list all current groups
	 * 
	 * @return List
	 */
	public List<GroupPrincipal> listGroups() throws AccessException;

	/**
	 * list users from this group
	 * 
	 * @param group
	 * @return List
	 */
	public List<UserPrincipal> listUsersInGroup(Principal group) throws AccessException;

	/**
	 * list all groups this user belongs to
	 * 
	 * @param user
	 * @return List
	 */
	public Set<GroupPrincipal> listGroupsForUser(Principal user) throws AccessException;

	/**
	 * adds user to group
	 * 
	 * @param user
	 * @param group
	 */
	public void addUserToGroup(Principal user, Principal group) throws AccessException;

	/**
	 * removes user from group
	 * 
	 * @param user
	 * @param group
	 */
	public void removeUserFromGroup(Principal user, Principal group) throws AccessException;

	/**
	 * remove group from groups
	 * 
	 * @param group
	 */
	public void deleteGroup(Principal group) throws AccessException;
}
