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

import java.io.Serializable;
import java.io.IOException;
import java.security.*;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * <P>
 * The name is the name of the custom permission. The naming
 * convention follows the  hierarchical property naming convention.
 * Also, an asterisk
 * may appear at the end of the name, following a ".", or by itself, to
 * signify a wildcard match. For example: "myaction.*" or "*" is valid,
 * "*myaction" or "a*b" is not valid.
 * <P>
 * <P>
 * The actions to be granted are passed to the constructor in a string containing 
 * a list of one or more comma-separated keywords. The possible keywords are
 * "read", "write", "execute", and "delete". Their meaning is defined as follows:
 * <P>
 * <DL> 
 *    <DT> read <DD> read permission
 *    <DT> write <DD> write permission
 *    <DT> execute 
 *    <DD> execute permission.
 *    <DT> delete
 *    <DD> delete permission.
 * </DL>
 * <P>
 * The actions string is converted to lowercase before processing.
 * <P>
 *
 * @see java.security.Permission
 * @see java.security.Permissions
 * @see java.security.PermissionCollection
 * @see java.lang.SecurityManager
 * 
 * @author E.F. Hillenius
 */

public final class AccessPermission extends BasicPermission {

	/**
	 * Execute action.
	 */
	private final static int EXECUTE = 0x1;
	/**
	 * Write action.
	 */
	private final static int WRITE   = 0x2;
	/**
	 * Read action.
	 */
	private final static int READ    = 0x4;
	/**
	 * Delete action.
	 */
	private final static int DELETE  = 0x8;

	/**
	 * All actions (read,write,execute,delete)
	 */
	private final static int ALL     = READ|WRITE|EXECUTE|DELETE;
	/**
	 * No actions.
	 */
	private final static int NONE    = 0x0;

	/**
	 * The actions mask.
	 *
	 */
	private transient int mask;

	/**
	 * The actions string.
	 *
	 * @serial 
	 */
	private String actions; // Left null as long as possible, then
							// created and re-used in the getAction function.

	/**
	 * initialize a AccessPermission object. Common to all constructors.
	 * Also called during de-serialization.
	 *
	 * @param mask the actions mask to use.
	 *
	 */

	private void init(int mask)
	{

	if ((mask & ALL) != mask)
		throw new IllegalArgumentException("invalid actions mask");

	if (mask == NONE)
		throw new IllegalArgumentException("invalid actions mask");

	if (getName() == null)
		throw new NullPointerException("name can't be null");

	this.mask = mask;
	}

	/**
	 * Creates a new AccessPermission object with the specified name.
	 * The name is the name of the custom action, and
	 * <i>actions</i> contains a comma-separated list of the
	 * desired actions granted on the property. Possible actions are
	 * "read" and "write".
	 *
	 * @param name the name of the AccessPermission.
	 * @param actions the actions string.
	 */

	public AccessPermission(String name, String actions)
	{
	super(name,actions);
	init(getMask(actions));
	}

	/**
	 * Checks if this AccessPermission object "implies" the specified
	 * permission.
	 * <P>
	 * More specifically, this method returns true if:<p>
	 * <ul>
	 * <li> <i>p</i> is an instanceof AccessPermission,<p>
	 * <li> <i>p</i>'s actions are a subset of this
	 * object's actions, and <p>
	 * <li> <i>p</i>'s name is implied by this object's
	 *      name. For example, "myaction.*" implies "myaction.anotherone".
	 * </ul>
	 * @param p the permission to check against.
	 *
	 * @return true if the specified permission is implied by this object,
	 * false if not.
	 */
	public boolean implies(Permission p) {
	if (!(p instanceof AccessPermission))
		return false;

	AccessPermission that = (AccessPermission) p;

	// we get the effective mask. i.e., the "and" of this and that.
	// They must be equal to that.mask for implies to return true.

	return ((this.mask & that.mask) == that.mask) && super.implies(that);
	}


	/**
	 * Checks two AccessPermission objects for equality. Checks that <i>obj</i> is
	 * a AccessPermission, and has the same name and actions as this object.
	 * <P>
	 * @param obj the object we are testing for equality with this object.
	 * @return true if obj is a AccessPermission, and has the same name and
	 * actions as this AccessPermission object.
	 */
	public boolean equals(Object obj) {
	if (obj == this)
		return true;

	if (! (obj instanceof AccessPermission))
		return false;

	AccessPermission that = (AccessPermission) obj;

	return (this.mask == that.mask) &&
		(this.getName().equals(that.getName()));
	}

	/**
	 * Returns the hash code value for this object.
	 * The hash code used is the hash code of this permissions name, that is,
	 * <code>getName().hashCode()</code>, where <code>getName</code> is
	 * from the Permission superclass.
	 *
	 * @return a hash code value for this object.
	 */

	public int hashCode() {
	return this.getName().hashCode();
	}


	/**
	 * Converts an actions String to an actions mask.
	 *
	 * @param action the action string.
	 * @return the actions mask.
	 */
	private static int getMask(String actions) {

		int mask = NONE;

		if (actions == null) {
			return mask;
		}

		char[] a = actions.toCharArray();

		int i = a.length - 1;
		if (i < 0)
			return mask;

		while (i != -1) {
			char c;

			// skip whitespace
			while ((i!=-1) && ((c = a[i]) == ' ' ||
					   c == '\r' ||
					   c == '\n' ||
					   c == '\f' ||
					   c == '\t'))
			i--;

			// check for the known strings
			int matchlen;

			if (i >= 3 && (a[i-3] == 'r' || a[i-3] == 'R') &&
				  (a[i-2] == 'e' || a[i-2] == 'E') &&
				  (a[i-1] == 'a' || a[i-1] == 'A') &&
				  (a[i] == 'd' || a[i] == 'D'))
			{
			matchlen = 4;
			mask |= READ;

			} else if (i >= 4 && (a[i-4] == 'w' || a[i-4] == 'W') &&
					 (a[i-3] == 'r' || a[i-3] == 'R') &&
					 (a[i-2] == 'i' || a[i-2] == 'I') &&
					 (a[i-1] == 't' || a[i-1] == 'T') &&
					 (a[i] == 'e' || a[i] == 'E'))
			{
			matchlen = 5;
			mask |= WRITE;

			} else if (i >= 6 && (a[i-6] == 'e' || a[i-6] == 'E') &&
							 (a[i-5] == 'x' || a[i-5] == 'X') &&
					 (a[i-4] == 'e' || a[i-4] == 'E') &&
					 (a[i-3] == 'c' || a[i-3] == 'C') &&
					 (a[i-2] == 'u' || a[i-2] == 'U') &&
					 (a[i-1] == 't' || a[i-1] == 'T') &&
					 (a[i] == 'e' || a[i] == 'E'))
			{
			matchlen = 7;
			mask |= EXECUTE;

			} else if (i >= 5 && (a[i-5] == 'd' || a[i-5] == 'D') &&
					 (a[i-4] == 'e' || a[i-4] == 'E') &&
					 (a[i-3] == 'l' || a[i-3] == 'L') &&
					 (a[i-2] == 'e' || a[i-2] == 'E') &&
					 (a[i-1] == 't' || a[i-1] == 'T') &&
					 (a[i] == 'e' || a[i] == 'E'))
			{
			matchlen = 6;
			mask |= DELETE;

			} else {
			// parse error
			throw new IllegalArgumentException(
				"invalid permission: " + actions);
			}

			// make sure we didn't just match the tail of a word
			// like "ackbarfaccept".  Also, skip to the comma.
			boolean seencomma = false;
			while (i >= matchlen && !seencomma) {
			switch(a[i-matchlen]) {
			case ',':
				seencomma = true;
				/*FALLTHROUGH*/
			case ' ': case '\r': case '\n':
			case '\f': case '\t':
				break;
			default:
				throw new IllegalArgumentException(
					"invalid permission: " + actions);
			}
			i--;
			}

			// point i at the location of the comma minus one (or -1).
			i -= matchlen;
	}

	return mask;
	}


	/**
	 * Return the canonical string representation of the actions.
	 * Always returns present actions in the following order: 
	 * read, write, execute, delete.
	 *
	 * @return the canonical string representation of the actions.
	 */
	static String getActions(int mask)
	{
		StringBuffer sb = new StringBuffer();
			boolean comma = false;

		if ((mask & READ) == READ) {
			comma = true;
			sb.append("read");
		}

		if ((mask & WRITE) == WRITE) {
			if (comma) sb.append(',');
				else comma = true;
			sb.append("write");
		}

		if ((mask & EXECUTE) == EXECUTE) {
			if (comma) sb.append(',');
				else comma = true;
			sb.append("execute");
		}

		if ((mask & DELETE) == DELETE) {
			if (comma) sb.append(',');
				else comma = true;
			sb.append("delete");
		}

		return sb.toString();
	}

	/**
	 * Returns the "canonical string representation" of the actions.
	 * That is, this method always returns present actions in the following order:
	 * read, write. For example, if this AccessPermission object
	 * allows both write and read actions, a call to <code>getActions</code>
	 * will return the string "read,write".
	 *
	 * @return the canonical string representation of the actions.
	 */
	public String getActions()
	{
	if (actions == null)
		actions = getActions(this.mask);

	return actions;
	}

	/**
	 * Return the current action mask.
	 * Used by the AccessPermissionCollection
	 *
	 * @return the actions mask.
	 */

	int getMask() {
	return mask;
	}

	/**
	 * Returns a new PermissionCollection object for storing
	 * AccessPermission objects.
	 * <p>
	 *
	 * @return a new PermissionCollection object suitable for storing
	 * AccessPermissions.
	 */

	public PermissionCollection newPermissionCollection() {
	return new AccessPermissionCollection();
	}

	/**
	 * WriteObject is called to save the state of the AccessPermission
	 * to a stream. The actions are serialized, and the superclass
	 * takes care of the name.
	 */

	private synchronized void writeObject(java.io.ObjectOutputStream s)
		throws IOException
	{
	// Write out the actions. The superclass takes care of the name
	// call getActions to make sure actions field is initialized
	if (actions == null)
		getActions();
	s.defaultWriteObject();
	}

	/**
	 * readObject is called to restore the state of the AccessPermission from
	 * a stream.
	 */
	private synchronized void readObject(java.io.ObjectInputStream s)
		 throws IOException, ClassNotFoundException
	{
	// Read in the action, then initialize the rest
	s.defaultReadObject();
	init(getMask(actions));
	}
}

/**
 * A AccessPermissionCollection stores a set of AccessPermission
 * permissions.
 *
 * @see java.security.Permission
 * @see java.security.Permissions
 * @see java.security.PermissionCollection
 *
 * @serial include
 */
final class AccessPermissionCollection extends PermissionCollection
implements Serializable
{

	/**
	 * Table of permissions.
	 *
	 * @serial
	 */
	private Hashtable permissions;

	/**
	 * Boolean saying if "*" is in the collection.
	 *
	 * @serial
	 */
	private boolean all_allowed;

	/**
	 * Create an empty AccessPermissions object.
	 *
	 */

	public AccessPermissionCollection() {
	permissions = new Hashtable(32);     // Capacity for default policy
	all_allowed = false;
	}

	/**
	 * Adds a permission to the AccessPermissions. The key for the hash is
	 * the name.
	 *
	 * @param permission the Permission object to add.
	 *
	 * @exception IllegalArgumentException - if the permission is not a
	 *                                       AccessPermission
	 *
	 * @exception SecurityException - if this AccessPermissionCollection
	 *                                object has been marked readonly
	 */

	public void add(Permission permission)
	{
	if (! (permission instanceof AccessPermission))
		throw new IllegalArgumentException("invalid permission: "+
						   permission);
	if (isReadOnly())
		throw new SecurityException("attempt to add a Permission to a readonly PermissionCollection");

	AccessPermission pp = (AccessPermission) permission;

	AccessPermission existing =
		(AccessPermission) permissions.get(pp.getName());

	if (existing != null) {
		int oldMask = existing.getMask();
		int newMask = pp.getMask();
		if (oldMask != newMask) {
		int effective = oldMask | newMask;
		String actions = AccessPermission.getActions(effective);
		permissions.put(pp.getName(),
			new AccessPermission(pp.getName(), actions));

		}
	} else {
		permissions.put(pp.getName(), permission);
	}

		if (!all_allowed) {
		if (pp.getName().equals("*"))
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
	if (! (permission instanceof AccessPermission))
		return false;

	AccessPermission pp = (AccessPermission) permission;
	AccessPermission x;

	int desired = pp.getMask();
	int effective = 0;

	// short circuit if the "*" Permission was added
	if (all_allowed) {
		x = (AccessPermission) permissions.get("*");
		if (x != null) {
		effective |= x.getMask();
		if ((effective & desired) == desired)
			return true;
		}
	}

	// strategy:
	// Check for full match first. Then work our way up the
	// name looking for matches on a.b.*

	String name = pp.getName();
	//System.out.println("check "+name);

	x = (AccessPermission) permissions.get(name);

	if (x != null) {
		// we have a direct hit!
		effective |= x.getMask();
		if ((effective & desired) == desired)
		return true;
	}

	// work our way up the tree...
	int last, offset;

	offset = name.length()-1;

	while ((last = name.lastIndexOf(".", offset)) != -1) {

		name = name.substring(0, last+1) + "*";
		//System.out.println("check "+name);
		x = (AccessPermission) permissions.get(name);

		if (x != null) {
		effective |= x.getMask();
		if ((effective & desired) == desired)
			return true;
		}
		offset = last -1;
	}

	// we don't have to check for "*" as it was already checked
	// at the top (all_allowed), so we just return false
	return false;
	}

	/**
	 * Returns an enumeration of all the AccessPermission objects in the
	 * container.
	 *
	 * @return an enumeration of all the AccessPermission objects.
	 */

	public Enumeration elements()
	{
	return permissions.elements();
	}
}
