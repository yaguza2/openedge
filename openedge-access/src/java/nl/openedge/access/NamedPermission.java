/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 * Copyright (c) 2003, Open Edge B.V.
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, 
 * this list of conditions and the following disclaimer. Redistributions 
 * in binary form must reproduce the above copyright notice, this list of 
 * conditions and the following disclaimer in the documentation and/or other 
 * materials provided with the distribution. Neither the name of OpenEdge B.V. 
 * nor the names of its contributors may be used to endorse or promote products 
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package nl.openedge.access;

import java.io.IOException;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * The name for a NamedPermission is the name of the given permission
 * (for example, "exit",
 * "setFactory", "print/queueJob", etc). The naming
 * convention follows a hierarchical naming convention.
 * An asterisk may appear by itself, or if immediately preceded by a "/"
 * may appear at the end of the name, to signify a wildcard match.
 * For example, "*" and "path/*" are valid, while "*path", "a*b",
 * and "path*" are not valid.
 * <P>
 * The action string (inherited from Permission) is unused.
 * <p>
 * <P>
 * @see java.security.Permission
 * @see java.security.Permissions
 * @see java.security.PermissionCollection
 * @see java.lang.SecurityManager
 *
 * @author E.F. Hillenius
 */
public class NamedPermission extends Permission implements java.io.Serializable
{

	// does this permission have a wildcard at the end?
	private transient boolean wildcard;

	// the name without the wildcard on the end
	private transient String path;

	/**
	 * initialize a NamedPermission object. Common to all constructors.
	 *
	 */

	private void init(String name)
	{
		if (name == null)
			throw new NullPointerException("name can't be null");

		if (name.equals(""))
		{
			throw new IllegalArgumentException("name can't be empty");
		}

		if (name.endsWith("/*") || name.equals("*"))
		{
			wildcard = true;
			if (name.length() == 1)
			{
				path = "";
			}
			else
			{
				path = name.substring(0, name.length() - 1);
			}
		}
		else
		{
			path = name;
		}
	}

	/**
	 * Creates a new NamedPermission with the specified name.
	 * Name is the symbolic name of the permission, such as
	 * "setFactory",
	 * "print/queueJob", or "topLevelWindow", etc.
	 *
	 * @param name the name of the NamedPermission.
	 *
	 * @throws NullPointerException if <code>name</code> is <code>null</code>.
	 * @throws IllegalArgumentException if <code>name</code> is empty.
	 */

	public NamedPermission(String name)
	{
		super(name);
		init(name);
	}

	/**
	 * Creates a new NamedPermission object with the specified name.
	 * The name is the symbolic name of the NamedPermission, and the
	 * actions String is currently unused.
	 *
	 * @param name the name of the NamedPermission.
	 * @param actions ignored.
	 *
	 * @throws NullPointerException if <code>name</code> is <code>null</code>.
	 * @throws IllegalArgumentException if <code>name</code> is empty.
	 */
	public NamedPermission(String name, String actions)
	{
		super(name);
		init(name);
	}

	/**
	 * Checks if the specified permission is "implied" by
	 * this object.
	 * <P>
	 * More specifically, this method returns true if:<p>
	 * <ul>
	 * <li> <i>p</i>'s class is the same as this object's class, and<p>
	 * <li> <i>p</i>'s name equals or (in the case of wildcards)
	 *      is implied by this object's
	 *      name. For example, "a/b/*" implies "a/b/c".
	 * </ul>
	 *
	 * @param p the permission to check against.
	 *
	 * @return true if the passed permission is equal to or
	 * implied by this permission, false otherwise.
	 */
	public boolean implies(Permission p)
	{
		if ((p == null) || (p.getClass() != getClass()))
			return false;

		NamedPermission that = (NamedPermission)p;

		if (this.wildcard)
		{
			if (that.wildcard)
				// one wildcard can imply another
				return that.path.startsWith(path);
			else
				// make sure ap.path is longer so a/b/* doesn't imply a/b
				return (that.path.length() > this.path.length()) 
						&& that.path.startsWith(this.path);
		}
		else
		{
			if (that.wildcard)
			{
				// a non-wildcard can't imply a wildcard
				return false;
			}
			else
			{
				return this.path.equals(that.path);
			}
		}
	}

	/**
	 * Checks two NamedPermission objects for equality.
	 * Checks that <i>obj</i>'s class is the same as this object's class
	 * and has the same name as this object.
	 * <P>
	 * @param obj the object we are testing for equality with this object.
	 * @return true if <i>obj</i> is a NamedPermission, and has the same name
	 *  as this NamedPermission object, false otherwise.
	 */
	public boolean equals(Object obj)
	{
		if (obj == this)
			return true;

		if ((obj == null) || (obj.getClass() != getClass()))
			return false;

		NamedPermission bp = (NamedPermission)obj;

		return getName().equals(bp.getName());
	}

	/**
	 * Returns the hash code value for this object.
	 * The hash code used is the hash code of the name, that is,
	 * <code>getName().hashCode()</code>, where <code>getName</code> is
	 * from the Permission superclass.
	 *
	 * @return a hash code value for this object.
	 */

	public int hashCode()
	{
		return this.getName().hashCode();
	}

	/**
	 * Returns the canonical string representation of the actions,
	 * which currently is the empty string "", since there are no actions for
	 * a NamedPermission.
	 *
	 * @return the empty string "".
	 */
	public String getActions()
	{
		return "";
	}

	/**
	 * Returns a new PermissionCollection object for storing NamedPermission
	 * objects.
	 * <p>
	 * A NamedPermissionCollection stores a collection of
	 * NamedPermission permissions.
	 *
	 * <p>NamedPermission objects must be stored in a manner that allows them
	 * to be inserted in any order, but that also enables the
	 * PermissionCollection <code>implies</code> method
	 * to be implemented in an efficient (and consistent) manner.
	 *
	 * @return a new PermissionCollection object suitable for
	 * storing NamedPermissions.
	 */

	public PermissionCollection newPermissionCollection()
	{
		return new NamedPermissionCollection();
	}

	/**
	 * readObject is called to restore the state of the NamedPermission from
	 * a stream. 
	 */
	private synchronized void readObject(java.io.ObjectInputStream s) 
				throws IOException, ClassNotFoundException
	{
		s.defaultReadObject();
		// init is called to initialize the rest of the values.
		init(getName());
	}
}

/**
 * A NamedPermissionCollection stores a collection
 * of NamedPermission permissions. NamedPermission objects
 * must be stored in a manner that allows them to be inserted in any
 * order, but enable the implies function to evaluate the implies
 * method in an efficient (and consistent) manner.
 *
 * A NamedPermissionCollection handles comparing a permission like "a/b/c/d/e"
 * with a Permission such as "a/b/*", or "*".
 *
 * @see java.security.Permission
 * @see java.security.Permissions
 * @see java.security.PermissionsImpl
 *
 * @serial include
 */

final class NamedPermissionCollection extends PermissionCollection 
		implements java.io.Serializable
{

	/**
	 * The NamedPermissions in this NamedPermissionCollection.
	 * All NamedPermissions in the collection must belong to the same class.
	 *
	 * @serial the Hashtable is indexed by the NamedPermission name
	 */
	private Hashtable permissions;

	/**
	 * This is set to <code>true</code> if this NamedPermissionCollection
	 * contains a NamedPermission with '*' as its permission name.
	 *
	 * @serial
	 */
	private boolean all_allowed;

	/**
	 * The class to which all NamedPermissions in this
	 * NamedPermissionCollection belongs.
	 *
	 * @serial
	 */
	private Class permClass;

	/**
	 * Create an empty NamedPermissionCollection object.
	 *
	 */

	public NamedPermissionCollection()
	{
		permissions = new Hashtable(11);
		all_allowed = false;
	}

	/**
	 * Adds a permission to the NamedPermissions. The key for the hash is
	 * permission.path.
	 *
	 * @param permission the Permission object to add.
	 *
	 * @exception IllegalArgumentException - if the permission is not a
	 *                                       NamedPermission, or if
	 *					     the permission is not of the
	 *					     same Class as the other
	 *					     permissions in this collection.
	 *
	 * @exception SecurityException - if this NamedPermissionCollection object
	 *                                has been marked readonly
	 */

	public void add(Permission permission)
	{
		if (!(permission instanceof NamedPermission))
			throw new IllegalArgumentException("invalid permission: " + permission);
		if (isReadOnly())
			throw new SecurityException(
				"attempt to add a Permission to a readonly PermissionCollection");

		NamedPermission np = (NamedPermission)permission;

		if (permissions.size() == 0)
		{
			// adding first permission
			permClass = np.getClass();
		}
		else
		{
			// make sure we only add new NamedPermissions of the same class
			if (np.getClass() != permClass)
				throw new IllegalArgumentException(
					"invalid permission: " + permission);
		}

		permissions.put(np.getName(), permission);
		if (!all_allowed)
		{
			if (np.getName().equals("*"))
				all_allowed = true;
		}
	}

	/**
	 * Check and see if this set of permissions implies the permissions
	 * expressed in "permission".
	 *
	 * @param p the Permission object to compare
	 *
	 * @return true if "permission" is a proper subset of a permission in
	 * the set, false if not.
	 */

	public boolean implies(Permission permission)
	{
		if (!(permission instanceof NamedPermission))
			return false;

		NamedPermission np = (NamedPermission)permission;

		// random subclasses of NamedPermission do not imply each other
		if (np.getClass() != permClass)
			return false;

		// short circuit if the "*" Permission was added
		if (all_allowed)
			return true;

		// strategy:
		// Check for full match first. Then work our way up the
		// path looking for matches on a/b//*

		String path = np.getName();
		//System.out.println("check "+path);

		Permission x = (Permission)permissions.get(path);

		if (x != null)
		{
			// we have a direct hit!
			return x.implies(permission);
		}

		// work our way up the tree...
		int last, offset;

		offset = path.length() - 1;

		while ((last = path.lastIndexOf("/", offset)) != -1)
		{

			path = path.substring(0, last + 1) + "*";
			//System.out.println("check "+path);
			x = (Permission)permissions.get(path);

			if (x != null)
			{
				return x.implies(permission);
			}
			offset = last - 1;
		}

		// we don't have to check for "*" as it was already checked
		// at the top (all_allowed), so we just return false
		return false;
	}

	/**
	 * Returns an enumeration of all the NamedPermission objects in the
	 * container.
	 *
	 * @return an enumeration of all the NamedPermission objects.
	 */

	public Enumeration elements()
	{
		return permissions.elements();
	}

	/**
	 * readObject is called to restore the state of the
	 * NamedPermissionCollection from a stream.
	 */
	private synchronized void readObject(java.io.ObjectInputStream s) 
				throws IOException, ClassNotFoundException
	{
		s.defaultReadObject();

		if (permClass == null)
		{
			// set permClass
			Enumeration e = permissions.elements();
			if (e.hasMoreElements())
			{
				Permission p = (Permission)e.nextElement();
				permClass = p.getClass();
			}
		}
	}
}
