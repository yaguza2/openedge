package nl.openedge.modules.impl.menumodule;

import java.security.Principal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Secorator for sets of principals. NOTE: though not strong typed, this set should never
 * contain other set elements other than Principals
 * 
 * @author Eelco Hillenius
 */
public final class PrincipalSet implements Set<Principal>
{
	private Set<Principal> decorated = null;

	public PrincipalSet(Set<Principal> toDecorate)
	{
		if (toDecorate == null)
		{
			this.decorated = new HashSet<Principal>();
		}
		else
		{
			this.decorated = toDecorate;
		}
	}

	@Override
	public int size()
	{
		return decorated.size();
	}

	@Override
	public void clear()
	{
		decorated.clear();
	}

	@Override
	public boolean isEmpty()
	{
		return decorated.isEmpty();
	}

	@Override
	public Object[] toArray()
	{
		return decorated.toArray();
	}

	@Override
	public boolean add(Principal o)
	{
		return decorated.add(o);
	}

	@Override
	public boolean remove(Object o)
	{
		return decorated.remove(o);
	}

	@Override
	public boolean addAll(Collection< ? extends Principal> c)
	{
		return decorated.addAll(c);
	}

	/**
	 * Checks if object o is contained in this set. Additionally, in case elements with
	 * name '*' are found (where * stands for 'valid for all instances'), the occurance
	 * will be checked on type instead of the equals method
	 * 
	 * @see java.util.Collection#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(Object o)
	{
		Iterator<Principal> i = iterator();
		if (o == null)
		{
			while (i.hasNext())
			{
				if (i.next() == null)
				{
					return true;
				}
			}
		}
		else
		{
			Principal that = (Principal) o;

			if (that.getName().equals("*"))
			{
				while (i.hasNext())
				{
					Principal p = i.next();
					// test on class instead of name
					if (p.getClass().isAssignableFrom(o.getClass()))
					{
						return true;
					}
				}
			}
			else
			{
				while (i.hasNext())
				{
					if (o.equals(i.next()))
					{
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean containsAll(Collection< ? > c)
	{
		Iterator< ? > e = c.iterator();
		while (e.hasNext())
			if (!contains(e.next()))
				return false;

		return true;
	}

	@Override
	public boolean removeAll(Collection< ? > c)
	{
		return decorated.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection< ? > c)
	{
		return decorated.retainAll(c);
	}

	@Override
	public Iterator<Principal> iterator()
	{
		return decorated.iterator();
	}

	@Override
	public <T> T[] toArray(T[] a)
	{
		return decorated.toArray(a);
	}
}
