package nl.openedge.access;

import java.security.Principal;

/**
 * This class implements the <code>Principal</code> interface and represents a group.
 * <p>
 * Principals such as this <code>GroupPrincipal</code> may be associated with a particular
 * <code>Subject</code> to augment that <code>Subject</code> with an additional identity.
 * Refer to the <code>Subject</code> class for more information on how to achieve this.
 * Authorization decisions can then be based upon the Principals associated with a
 * <code>Subject</code>.
 * 
 * @author E.F. Hillenius
 * @see java.security.Principal
 * @see javax.security.auth.Subject
 */
public class GroupPrincipal implements Principal, java.io.Serializable
{
	private static final long serialVersionUID = 1L;

	protected String name;

	public GroupPrincipal()
	{
	}

	/**
	 * Create a GroupPrincipal with a group name.
	 * 
	 * @param name
	 *            the name of this group.
	 * 
	 * @exception NullPointerException
	 *                if the <code>name</code> is <code>null</code>.
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
	 * Return the name for this <code>GroupPrincipal</code>.
	 * 
	 * @return the name for this <code>GroupPrincipal</code>
	 */
	@Override
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
	 * Return a string representation of this <code>GroupPrincipal</code>.
	 * 
	 * @return a string representation of this <code>GroupPrincipal</code>.
	 */
	@Override
	public String toString()
	{
		return "GroupPrincipal: " + name;
	}

	/**
	 * Compares the specified Object with this <code>GroupPrincipal</code> for equality.
	 * Returns true if the given object is also a <code>GroupPrincipal</code> and the two
	 * GroupPrincipals have the same username.
	 * 
	 * @param o
	 *            Object to be compared for equality with this <code>GroupPrincipal</code>
	 * 
	 * @return true if the specified Object is equal equal to this
	 *         <code>GroupPrincipal</code>.
	 */
	@Override
	public boolean equals(Object o)
	{
		if (o == null)
			return false;

		if (this == o)
			return true;

		if (!(o instanceof GroupPrincipal))
			return false;
		GroupPrincipal that = (GroupPrincipal) o;

		if (this.getName().equals(that.getName()))
			return true;
		return false;
	}

	/**
	 * Return a hash code for this <code>GroupPrincipal</code>.
	 * 
	 * @return a hash code for this <code>GroupPrincipal</code>.
	 */
	@Override
	public int hashCode()
	{
		return name.hashCode();
	}
}
