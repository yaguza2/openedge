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

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

/**
 * <p> This class implements the <code>Principal</code> interface
 * and represents a user.
 *
 * <p> Principals such as this <code>UserPrincipal</code>
 * may be associated with a particular <code>Subject</code>
 * to augment that <code>Subject</code> with an additional
 * identity.  Refer to the <code>Subject</code> class for more information
 * on how to achieve this.  Authorization decisions can then be based upon 
 * the Principals associated with a <code>Subject</code>.
 * 
 * @author E.F. Hillenius
 * @see java.security.Principal
 * @see javax.security.auth.Subject
 */
public class UserPrincipal implements Principal, java.io.Serializable
{

	/** name */
	protected String name;

	/** encrypted password... carefull with this */
	protected String password;

	/** roles that user is member of */
	protected Set roles;

	/** roles that user is member of */
	protected Set groups;

	/**
	 * Default constructor.
	 */
	public UserPrincipal()
	{
		// do nothing here
	}

	/**
	 * Create a UserPrincipal with a username.
	 *
	 * <p>
	 *
	 * @param name the username for this user.
	 *
	 * @exception NullPointerException if the <code>name</code>
	 *			is <code>null</code>.
	 */
	public UserPrincipal(String name)
	{

		if (name == null)
		{
			throw new NullPointerException("name is not allowed to be null");
		}
		this.name = name;
	}

	/**
	 * Return the username for this <code>UserPrincipal</code>.
	 *
	 * <p>
	 *
	 * @return the username for this <code>UserPrincipal</code>
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @return String
	 */
	public String getPassword()
	{
		return password;
	}

	/**
	 * @param password
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}

	/**
	 * @return List
	 */
	public Set getRoles()
	{
		return roles;
	}

	/**
	 * Sets the roles.
	 * @param roles The roles to set
	 */
	public void setRoles(Set roles)
	{
		this.roles = roles;
	}

	/**
	 * add role
	 * @param role
	 */
	public void addRole(Principal role)
	{
		if (role instanceof RolePrincipal)
		{
			if (this.roles == null)
			{
				this.roles = new HashSet();
			}
			this.roles.add(role);
		}
	}

	/**
	 * remove role
	 * @param role
	 */
	public void removeRole(Principal role)
	{
		if (this.roles != null && (role instanceof RolePrincipal))
		{
			this.roles.remove(role);
		}
	}

	/**
	 * does this user has given role?
	 * @param role
	 */
	public boolean containsRole(Principal role)
	{
		if (this.roles != null && (role instanceof RolePrincipal))
		{
			return this.roles.contains(role);
		}
		else
		{
			return false;
		}
	}

	/**
	 * @return Set
	 */
	public Set getGroups()
	{
		return groups;
	}

	/**
	 * @param groups
	 */
	public void setGroups(Set groups)
	{
		this.groups = groups;
	}

	/**
	 * add group
	 * @param group
	 */
	public void addGroup(Principal group)
	{
		if (group instanceof GroupPrincipal)
		{
			if (this.groups == null)
			{
				this.groups = new HashSet();
			}
			this.groups.add(group);
		}
	}

	/**
	 * remove group
	 * @param group
	 */
	public void removeGroup(Principal group)
	{
		if (this.groups != null && (group instanceof GroupPrincipal))
		{
			this.groups.remove(group);
		}
	}

	/**
	 * does this user has given role?
	 * @param group
	 */
	public boolean containsGroup(GroupPrincipal group)
	{
		if (this.groups != null && (group instanceof GroupPrincipal))
		{
			return this.groups.contains(group);
		}
		else
		{
			return false;
		}
	}

	/**
	 * Return a string representation of this <code>UserPrincipal</code>.
	 *
	 * <p>
	 *
	 * @return a string representation of this <code>UserPrincipal</code>.
	 */
	public String toString()
	{
		return "UserPrincipal: " + name;
	}

	/**
	 * Compares the specified Object with this <code>UserPrincipal</code>
	 * for equality.  Returns true if the given object is also a
	 * <code>UserPrincipal</code> and the two UserPrincipals
	 * have the same username.
	 *
	 * <p>
	 *
	 * @param o Object to be compared for equality with this
	 *		<code>UserPrincipal</code>.
	 *
	 * @return true if the specified Object is equal equal to this
	 *		<code>UserPrincipal</code>.
	 */
	public boolean equals(Object o)
	{
		if (o == null)
			return false;

		if (this == o)
			return true;

		if (!(o instanceof UserPrincipal))
			return false;
		UserPrincipal that = (UserPrincipal)o;

		if (this.getName().equals(that.getName()))
			return true;
		return false;
	}

	/**
	 * Return a hash code for this <code>UserPrincipal</code>.
	 *
	 * <p>
	 *
	 * @return a hash code for this <code>UserPrincipal</code>.
	 */
	public int hashCode()
	{
		return name.hashCode();
	}

}
