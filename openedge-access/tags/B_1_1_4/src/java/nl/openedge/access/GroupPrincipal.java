/*
 * $Id$
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

/**
 * <p> This class implements the <code>Principal</code> interface
 * and represents a group.
 *
 * <p> Principals such as this <code>RolePrincipal</code>
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
public class GroupPrincipal implements Principal, java.io.Serializable
{

	/** name */
	protected String name;

	/**
	 * Default constructor.
	 */
	public GroupPrincipal()
	{
		// do nothing here
	}

	/**
	 * Create a RolePrincipal with a group name.
	 *
	 * <p>
	 *
	 * @param name the name of this group.
	 *
	 * @exception NullPointerException if the <code>name</code>
	 *			is <code>null</code>.
	 */
	public GroupPrincipal(String name)
	{

		if (name == null)
		{
			throw new NullPointerException("name is not allowed to be null");
		}
		this.name = name;
	}

	/**
	 * Return the name for this <code>RolePrincipal</code>.
	 *
	 * <p>
	 *
	 * @return the name for this <code>RolePrincipal</code>
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
	 * Return a string representation of this <code>RolePrincipal</code>.
	 *
	 * <p>
	 *
	 * @return a string representation of this <code>RolePrincipal</code>.
	 */
	public String toString()
	{
		return "RolePrincipal: " + name;
	}

	/**
	 * Compares the specified Object with this <code>RolePrincipal</code>
	 * for equality.  Returns true if the given object is also a
	 * <code>RolePrincipal</code> and the two GroupPrincipals
	 * have the same username.
	 *
	 * <p>
	 *
	 * @param o Object to be compared for equality with this
	 *		<code>RolePrincipal</code>.
	 *
	 * @return true if the specified Object is equal equal to this
	 *		<code>RolePrincipal</code>.
	 */
	public boolean equals(Object o)
	{
		if (o == null)
			return false;

		if (this == o)
			return true;

		if (!(o instanceof GroupPrincipal))
			return false;
		GroupPrincipal that = (GroupPrincipal)o;

		if (this.getName().equals(that.getName()))
			return true;
		return false;
	}

	/**
	 * Return a hash code for this <code>RolePrincipal</code>.
	 *
	 * <p>
	 *
	 * @return a hash code for this <code>RolePrincipal</code>.
	 */
	public int hashCode()
	{
		return name.hashCode();
	}

}
