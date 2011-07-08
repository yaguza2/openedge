package nl.openedge.access;

import java.security.Principal;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * This class implements the <code>Principal</code> interface and represents a user.
 * 
 * <p>
 * Principals such as this <code>UserPrincipal</code> may be associated with a particular
 * <code>Subject</code> to augment that <code>Subject</code> with an additional identity.
 * Refer to the <code>Subject</code> class for more information on how to achieve this.
 * Authorization decisions can then be based upon the Principals associated with a
 * <code>Subject</code>.
 * 
 * @author E.F. Hillenius
 * @see java.security.Principal
 * @see javax.security.auth.Subject
 */
public class UserPrincipal implements Principal, java.io.Serializable
{
	private static final long serialVersionUID = 1L;

	protected String name;

	/** encrypted password... carefull with this */
	protected String password;

	/** roles that user is member of */
	protected Set<RolePrincipal> roles;

	/** roles that user is member of */
	protected Set<GroupPrincipal> groups;

	/** the prefered locale setting */
	protected Locale preferedLocale = null;

	public UserPrincipal()
	{
	}

	/**
	 * Create a UserPrincipal with a username.
	 * 
	 * @param name
	 *            the username for this user.
	 * 
	 * @exception NullPointerException
	 *                if the <code>name</code> is <code>null</code>.
	 */
	public UserPrincipal(String name)
	{
		if (name == null)
		{
			throw new NullPointerException("name is not allowed to be null");
		}
		this.name = name;
	}

	@Override
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public Set<RolePrincipal> getRoles()
	{
		return roles;
	}

	public void setRoles(Set<RolePrincipal> roles)
	{
		this.roles = roles;
	}

	public void addRole(Principal role)
	{
		if (role instanceof RolePrincipal)
		{
			RolePrincipal rolePrincipal = (RolePrincipal) role;
			if (this.roles == null)
			{
				this.roles = new HashSet<RolePrincipal>();
			}
			this.roles.add(rolePrincipal);
		}
	}

	public void removeRole(Principal role)
	{
		if (this.roles != null && (role instanceof RolePrincipal))
		{
			this.roles.remove(role);
		}
	}

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

	public Set<GroupPrincipal> getGroups()
	{
		return groups;
	}

	public void setGroups(Set<GroupPrincipal> groups)
	{
		this.groups = groups;
	}

	public void addGroup(Principal group)
	{
		if (group instanceof GroupPrincipal)
		{
			GroupPrincipal groupPrincipal = (GroupPrincipal) group;
			if (this.groups == null)
			{
				this.groups = new HashSet<GroupPrincipal>();
			}
			this.groups.add(groupPrincipal);
		}
	}

	public void removeGroup(Principal group)
	{
		if (this.groups != null && (group instanceof GroupPrincipal))
		{
			this.groups.remove(group);
		}
	}

	public boolean containsGroup(GroupPrincipal group)
	{
		if (this.groups != null && group != null)
		{
			return this.groups.contains(group);
		}
		else
		{
			return false;
		}
	}

	@Override
	public String toString()
	{
		return "UserPrincipal: " + name;
	}

	/**
	 * Compares the specified Object with this <code>UserPrincipal</code> for equality.
	 * Returns true if the given object is also a <code>UserPrincipal</code> and the two
	 * UserPrincipals have the same username.
	 * 
	 * @param o
	 *            Object to be compared for equality with this <code>UserPrincipal</code>.
	 * 
	 * @return true if the specified Object is equal equal to this
	 *         <code>UserPrincipal</code>.
	 */
	@Override
	public boolean equals(Object o)
	{
		if (o == null)
			return false;

		if (this == o)
			return true;

		if (!(o instanceof UserPrincipal))
			return false;
		UserPrincipal that = (UserPrincipal) o;

		if (this.getName().equals(that.getName()))
			return true;
		return false;
	}

	@Override
	public int hashCode()
	{
		return name.hashCode();
	}

	public Locale getPreferedLocale()
	{
		return preferedLocale;
	}

	public void setPreferedLocale(Locale locale)
	{
		preferedLocale = locale;
	}
}
