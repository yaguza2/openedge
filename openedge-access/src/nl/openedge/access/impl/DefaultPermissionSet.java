package nl.openedge.access.impl;

import java.util.ArrayList;
import java.util.List;

import nl.openedge.access.Permission;
import nl.openedge.access.PermissionSet;

/**
 * @author vries
 * 
 */
public class DefaultPermissionSet implements PermissionSet {
	
	private List permissions = new ArrayList();

	/**
	 * @see nl.openedge.security.PermissionSet#addPermission(nl.openedge.security.Permission)
	 */
	public void addPermission(Permission permission) {
		permissions.add(permission);
	}

	/**
	 * @see nl.openedge.security.PermissionSet#hasPermission(nl.openedge.security.Permission)
	 */
	public boolean hasPermission(Permission permission) {
		return permissions.contains(permission);
	}

	/**
	 * @see nl.openedge.security.PermissionSet#removePermission(nl.openedge.security.Permission)
	 */
	public void removePermission(Permission permission) {
		permissions.remove(permission);
	}

	/**
	 * @see nl.openedge.security.PermissionSet#getPermissions()
	 */
	public List getPermissions() {
		return permissions;
	}
}
