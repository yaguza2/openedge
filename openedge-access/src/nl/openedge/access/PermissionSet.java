package nl.openedge.access;

/**
 * holder for permissions
 * @author vries, hillenius  
 */
public interface PermissionSet {

	/** 
	 * checks if this set has the requested permission
	 * @param permission
	 * @return boolean
	 */
	public boolean hasPermission(Permission permission);
	
}
