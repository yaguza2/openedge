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
 * Hillenius
 * @see java.security.Principal
 * @see javax.security.auth.Subject
 */
public class RolePrincipal implements Principal, java.io.Serializable {

	/** name */
	protected String name;

	
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
	public RolePrincipal(String name) {
		
		if (name == null) {
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
	public String getName() {
		return name;
	}
	
	/**
	 * Return a string representation of this <code>RolePrincipal</code>.
	 *
	 * <p>
	 *
	 * @return a string representation of this <code>RolePrincipal</code>.
	 */
	public String toString() {
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
	public boolean equals(Object o) {
		if (o == null)
			return false;
		
			if (this == o)
				return true;
		 
			if (!(o instanceof RolePrincipal))
				return false;
			RolePrincipal that = (RolePrincipal)o;
		
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
	public int hashCode() {
		return name.hashCode();
	}

}
