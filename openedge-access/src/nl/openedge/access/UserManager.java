	/*
 * Created on 21-mrt-2003
 *
 */
package nl.openedge.access;

import java.util.List;
import java.util.Map;

/**
 * @author vries
 *
 * this manager manages the users and groups, it can create, and
 * remove users and groups, and add users to groups
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
	 * creates a new GroupPrincipal object
	 * @param id
	 * @param name
	 * @return GroupPrincipal
	 */
	public GroupPrincipal createGroup(String name) throws AccessException;
	
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
	 * returns a group with this id
	 * @param id
	 * @return GroupPrincipal
	 */	
	public GroupPrincipal getGroup(String name) throws AccessException;
	
	/**
	 * list all current users
	 * @return List
	 */
	public List listUsers() throws AccessException;
	
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
	public List listUsersInGroup(GroupPrincipal group) throws AccessException;
	
	/**
	 * list all groups this user belongs to
	 * @param user
	 * @return List
	 */
	public List listGroupsForUser(UserPrincipal user) throws AccessException;
	
	/**
	 * checks if user is in group group
	 * @param user
	 * @param group
	 * @return boolean
	 */
	public boolean isUserInGroup(UserPrincipal user, GroupPrincipal group) throws AccessException;
	
	/**
	 * adds user to group
	 * @param user
	 * @param group
	 */	
	public void addUserToGroup(UserPrincipal user, GroupPrincipal group) throws AccessException;
	
	/**
	 * removes user from group
	 * @param user
	 * @param group
	 */
	public void removeUserFromGroup(UserPrincipal user, GroupPrincipal group) throws AccessException;
	
	/**
	 * remove user from users
	 * @param user
	 */
	public void deleteUser(UserPrincipal user) throws AccessException;
	
	/**
	 * remove group from groups
	 * @param group
	 */
	public void deleteGroup(GroupPrincipal group) throws AccessException;
}
