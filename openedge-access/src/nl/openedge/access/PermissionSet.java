package nl.openedge.access;

import java.util.List;

/**
 * holds all permissions added to this set
 * @author vries  
 */
public interface PermissionSet {
	
	/**
	 * adds a permission to this set
	 * @param permission
	 */
	public void addPermission(Permission permission);

	/**
	 * removes a permission from this set
	 * @param permission
	 */
	public void removePermission(Permission permission);

	/** 
	 * checks if this set has the requested permission
	 * @param permission
	 * @return boolean
	 */
	public boolean hasPermission(Permission permission);
	
	/**
	 * returns all permissions as a List
	 * @return List
	 */	
	public List getPermissions();
}
