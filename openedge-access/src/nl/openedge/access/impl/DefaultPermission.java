/*
 * Created on 4-apr-2003
 */
package nl.openedge.access.impl;

import nl.openedge.access.Permission;


/**
 * @author Hillenius
 * $Id$
 */
public class DefaultPermission extends Permission {

	protected int permission;

	/**
	 * construct with int permission
	 */
	public DefaultPermission(int permission) {
		this.permission = permission;
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object other) {
		if(other == null || !(other instanceof DefaultPermission)) return false;
		DefaultPermission o = (DefaultPermission)other;
		return(permission == o.permission);
	}

}
