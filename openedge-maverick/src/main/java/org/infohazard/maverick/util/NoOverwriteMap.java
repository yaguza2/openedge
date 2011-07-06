/*
 * $Id: NoOverwriteMap.java,v 1.1 2002/01/03 00:51:06 lhoriman Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/util/NoOverwriteMap.java,v $
 */

package org.infohazard.maverick.util;

import java.util.Map;
import java.util.Set;
import java.util.Collection;
import java.util.Iterator;

/**
 * Map wrapper which prevents overwriting existing keys.
 */
public class NoOverwriteMap implements Map
{
	/**
	 */
	public static class OverwriteException extends IllegalArgumentException
	{
		/** */
		protected Object key;

		/** */
		public OverwriteException(Object key)
		{
			super("Tried to overwrite key:  " + key);

			this.key = key;
		}

		/** */
		public Object getDuplicateKey()
		{
			return this.key;
		}
	}

	/**
	 */
	protected Map wrapped;

	/**
	 */
	public NoOverwriteMap(Map wrap)
	{
		this.wrapped = wrap;
	}

	/**
	 */
	public int size()					{ return wrapped.size(); }
	public boolean isEmpty()			{ return wrapped.isEmpty(); }
	public boolean containsKey(Object key)		{ return wrapped.containsKey(key); }
	public boolean containsValue(Object value)	{ return wrapped.containsValue(value); }
	public Object get(Object key)		{ return wrapped.get(key); }
	public Object remove(Object key)	{ return wrapped.remove(key); }
	public void clear()					{ wrapped.clear(); }
	public Set keySet()					{ return wrapped.keySet(); }
	public Collection values()			{ return wrapped.values(); }
	public Set entrySet()				{ return wrapped.entrySet(); }
	public boolean equals(Object o)		{ return wrapped.equals(o); }
	public int hashCode()				{ return wrapped.hashCode(); }

	/**
	 * Prevents overwriting existing keys.
	 * @throws OverwriteException if the key is already present.
	 */
	public Object put(Object key, Object value)
	{
		if (this.containsKey(key))
			throw new OverwriteException(key);

		return this.wrapped.put(key, value);
	}

	/**
	 * Prevents overwriting existing keys.
	 * @throws OverwriteException if the key is already present.
	 */
	public void putAll(Map t)
	{
		Iterator it = t.entrySet().iterator();

		while (it.hasNext())
		{
			Map.Entry entry = (Map.Entry)it.next();
			this.put(entry.getKey(), entry.getValue());
		}
	}
}
