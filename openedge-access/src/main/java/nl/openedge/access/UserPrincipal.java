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
 * Hillenius
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

}
