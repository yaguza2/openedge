package nl.openedge.access;

import java.security.BasicPermission;

/**
 * The name for a BasicPermission is the name of the given permission
 * (for example, "exit",
 * "setFactory", "print.queueJob", etc). The naming
 * convention follows the  hierarchical property naming convention.
 * An asterisk may appear by itself, or if immediately preceded by a "."
 * may appear at the end of the name, to signify a wildcard match.
 * For example, "*" and "java.*" are valid, while "*java", "a*b",
 * and "java*" are not valid.
 * <P>
 * The action string (inherited from Permission) is unused.
 * <p>
 * <P>
 * @see java.security.Permission
 * @see java.security.Permissions
 * @see java.security.PermissionCollection
 * @see java.lang.RuntimePermission
 * @see java.security.SecurityPermission
 * @see java.util.PropertyPermission
 * @see java.awt.AWTPermission
 * @see java.net.NetPermission
 * @see java.lang.SecurityManager
 *
 * $Id$
 */
public class NamedPermission extends BasicPermission {

	/**
	 * Creates a new NamedPermission with the specified name.
	 * Name is the symbolic name of the permission, such as
	 * "setFactory",
	 * "print.queueJob", or "topLevelWindow", etc.
	 *
	 * @param name the name of the BasicPermission.
	 *
	 * @throws NullPointerException if <code>name</code> is <code>null</code>.
	 * @throws IllegalArgumentException if <code>name</code> is empty.
	 */
	public NamedPermission(String name) {
		super(name);
	}
	
	/**
	 * Creates a new NamedPermission object with the specified name.
	 * The name is the symbolic name of the BasicPermission, and the
	 * actions String is currently unused.
	 *
	 * @param name the name of the BasicPermission.
	 * @param actions ignored.
	 *
	 * @throws NullPointerException if <code>name</code> is <code>null</code>.
	 * @throws IllegalArgumentException if <code>name</code> is empty.
	 */
	public NamedPermission(String name, String action) {
		super(name, action);
	}

}
