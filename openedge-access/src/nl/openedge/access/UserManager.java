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
public interface UserManager {
	
	/**
	 * creates a new User object
	 * @param id
	 * @param name
	 * @return User
	 */
	public User createUser(String name);
	
	/**
	 * creates a new Group object
	 * @param id
	 * @param name
	 * @return Group
	 */
	public Group createGroup(String name);
	
	/**
	 * returns a user with this id
	 * @param id
	 * @return User
	 */
	public User getUser(Integer id);
	
	/**
	 * returns a group with this id
	 * @param id
	 * @return Group
	 */	
	public Group getGroup(Integer id);
	
	/**
	 * list all current users
	 * @return List
	 */
	public List listUsers();
	
	/**
	 * list all current groups
	 * @return List
	 */
	public List listGroups();
	
	/**
	 * list users from this group
	 * @param group
	 * @return List
	 */
	public List listUsersInGroup(Group group);
	
	/**
	 * list all groups this user belongs to
	 * @param user
	 * @return List
	 */
	public List listGroupsForUsers(User user);
	
	/**
	 * checks if user is in group group
	 * @param user
	 * @param group
	 * @return boolean
	 */
	public boolean isUserInGroup(User user, Group group);
	
	/**
	 * adds user to group
	 * @param user
	 * @param group
	 */	
	public void addUserToGroup(User user, Group group);
	
	/**
	 * removes user from group
	 * @param user
	 * @param group
	 */
	public void removeUserFromGroup(User user, Group group);
	
	/**
	 * remove user from users
	 * @param user
	 */
	public void removeUser(User user);
	
	/**
	 * remove group from groups
	 * @param group
	 */
	public void removeGroup(Group group);
}
