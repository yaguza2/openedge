	/*
 * Created on 21-mrt-2003
 *
 */
package nl.openedge.access;

import java.util.List;

/**
 * @author vries
 *
 * this manager manages the users and groups, it can create, and
 * remove users and groups, and add users to groups
 */
public interface UserManager extends Configurable {
	
	/**
	 * creates a new User object
	 * @param id
	 * @param name
	 * @param password
	 * @return User
	 */
	public User createUser(String name, String password) throws AccessException;
	
	/**
	 * set password to newPassword
	 * @param user
	 * @param newPassword
	 * @throws AccessException
	 */
	public void resetPassword(User user, String newPassword) throws AccessException;
	
	/**
	 * creates a new Group object
	 * @param id
	 * @param name
	 * @return Group
	 */
	public Group createGroup(String name) throws AccessException;
	
	/**
	 * returns a user with this name
	 * @param name
	 * @return User
	 */
	public User getUser(String name) throws AccessException;
	
	/**
	 * returns a group with this id
	 * @param id
	 * @return Group
	 */	
	public Group getGroup(String name) throws AccessException;
	
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
	public List listUsersInGroup(Group group) throws AccessException;
	
	/**
	 * list all groups this user belongs to
	 * @param user
	 * @return List
	 */
	public List listGroupsForUser(User user) throws AccessException;
	
	/**
	 * checks if user is in group group
	 * @param user
	 * @param group
	 * @return boolean
	 */
	public boolean isUserInGroup(User user, Group group) throws AccessException;
	
	/**
	 * adds user to group
	 * @param user
	 * @param group
	 */	
	public void addUserToGroup(User user, Group group) throws AccessException;
	
	/**
	 * removes user from group
	 * @param user
	 * @param group
	 */
	public void removeUserFromGroup(User user, Group group) throws AccessException;
	
	/**
	 * remove user from users
	 * @param user
	 */
	public void removeUser(User user) throws AccessException;
	
	/**
	 * remove group from groups
	 * @param group
	 */
	public void removeGroup(Group group) throws AccessException;
}
