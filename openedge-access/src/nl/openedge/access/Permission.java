/*
 * Created on 21-mrt-2003
 *
 */
package nl.openedge.access;

/**
 * @author vries
 * interface for own definition of a permission, with equals for
 * comparing  
 */
public abstract class Permission {
	
	/**
	 * compares permission with other permission
	 * @param otherPermission
	 * @return boolean
	 */
	public abstract boolean equals(Object permission);
}
