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
package nl.openedge.modules.impl.menumodule;

import java.security.Principal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * decorator for sets of principals.
 * NOTE: though not strong typed, this set should 
 * never contain other set elements other than Principals
 * @author Eelco Hillenius
 */
public final class PrincipalSet implements Set
{
 	/** decorated instance. */
	private Set decorated = null;
	
	/**
	 * construct with set to decorate.
	 * @param toDecorate set to decorate
	 */
	public PrincipalSet(Set toDecorate)
	{
		if(toDecorate == null)
		{
			this.decorated = new HashSet();
		}
		else
		{
			this.decorated = toDecorate;	
		}
	}

	/**
	 * @see java.util.Collection#size()
	 */
	public int size()
	{
		return decorated.size();
	}

	/**
	 * @see java.util.Collection#clear()
	 */
	public void clear()
	{
		decorated.clear();	
	}

	/**
	 * @see java.util.Collection#isEmpty()
	 */
	public boolean isEmpty()
	{
		return decorated.isEmpty();
	}

	/**
	 * @see java.util.Collection#toArray()
	 */
	public Object[] toArray()
	{
		return decorated.toArray();
	}

	/**
	 * @see java.util.Collection#add(java.lang.Object)
	 */
	public boolean add(Object o)
	{
		return decorated.add(o);
	}

	/**
	 * @see java.util.Collection#remove(java.lang.Object)
	 */
	public boolean remove(Object o)
	{
		return decorated.remove(o);
	}

	/**
	 * @see java.util.Collection#addAll(java.util.Collection)
	 */
	public boolean addAll(Collection c)
	{
		return decorated.addAll(c);
	}
	
	/**
	 * Checks if object o is contained in this set.
	 * Additionally, in case elements with name '*' are found 
	 * (where * stands for 'valid for all instances'),
	 * the occurance will be checked on type instead of the equals method
	 * @see java.util.Collection#contains(java.lang.Object)
	 */
	public boolean contains(Object o)
	{
		Iterator i = iterator();
		if (o==null) 
		{
			while (i.hasNext())
			{
				if (i.next()==null)
				{
					return true;	
				}
			}
		} 
		else 
		{
			Principal that = (Principal)o;
			
			if(that.getName().equals("*"))
			{
				while (i.hasNext())
				{
					Principal p = (Principal)i.next();
					// test on class instead of name
					if(p.getClass().isAssignableFrom(o.getClass()))
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

	/**
	 * @see java.util.Collection#containsAll(java.util.Collection)
	 */
	public boolean containsAll(Collection c)
	{
		Iterator e = c.iterator();
		while (e.hasNext())
			if(!contains(e.next()))
			return false;

		return true;
	}

	/**
	 * @see java.util.Collection#removeAll(java.util.Collection)
	 */
	public boolean removeAll(Collection c)
	{
		return decorated.removeAll(c);
	}

	/**
	 * @see java.util.Collection#retainAll(java.util.Collection)
	 */
	public boolean retainAll(Collection c)
	{
		return decorated.retainAll(c);
	}

	/**
	 * @see java.util.Collection#iterator()
	 */
	public Iterator iterator()
	{
		return decorated.iterator();
	}

	/**
	 * @see java.util.Collection#toArray(java.lang.Object[])
	 */
	public Object[] toArray(Object[] a)
	{
		return decorated.toArray(a);
	}

}
