/*
 * $Header$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package nl.openedge.access;

import java.security.Principal;
import java.util.List;
import java.util.Map;

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
public class UserPrincipal implements Principal, java.io.Serializable {

	/** name */
	protected String name;
	
	/** attributes for this user */
	protected Map attributes;
	
	/** roles that user is member of */
	protected List roles;
	
	/** possibly the active role for this user */
	protected RolePrincipal activeRole;
	
	/**
	 * Default constructor.
	 */
	public UserPrincipal() {
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
	public UserPrincipal(String name) {
		
		if (name == null) {
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
	public String getName() {
		return name;
	}
	
	/**
	 * Return a string representation of this <code>UserPrincipal</code>.
	 *
	 * <p>
	 *
	 * @return a string representation of this <code>UserPrincipal</code>.
	 */
	public String toString() {
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
	public boolean equals(Object o) {
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
	public int hashCode() {
		return name.hashCode();
	}

	/**
	 * @return Map
	 */
	public Map getAttributes() {
		return attributes;
	}

	/**
	 * @return List
	 */
	public List getRoles() {
		return roles;
	}

	/**
	 * Sets the attributes.
	 * @param attributes The attributes to set
	 */
	public void setAttributes(Map attributes) {
		this.attributes = attributes;
	}

	/**
	 * Sets the roles.
	 * @param roles The roles to set
	 */
	public void setRoles(List groups) {
		this.roles = groups;
	}

	/**
	 * @return RolePrincipal
	 */
	public RolePrincipal getActiveRole() {
		return activeRole;
	}

	/**
	 * Sets the activeRole.
	 * @param activeRole The activeRole to set
	 */
	public void setActiveRole(RolePrincipal activeRole) {
		this.activeRole = activeRole;
	}

}
