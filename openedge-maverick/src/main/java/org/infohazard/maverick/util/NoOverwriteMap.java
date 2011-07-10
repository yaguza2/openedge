/*
 * $Id: NoOverwriteMap.java,v 1.1 2002/01/03 00:51:06 lhoriman Exp $
 * $Source: /cvsroot-fuse/mav/maverick/src/java/org/infohazard/maverick/util/NoOverwriteMap.java,v $
 */

package org.infohazard.maverick.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Map wrapper which prevents overwriting existing keys.
 */
public class NoOverwriteMap<K, V> implements Map<K, V>
{
	public static class OverwriteException extends IllegalArgumentException
	{
		private static final long serialVersionUID = 1L;

		protected Object key;

		public OverwriteException(Object key)
		{
			super("Tried to overwrite key:  " + key);

			this.key = key;
		}

		public Object getDuplicateKey()
		{
			return this.key;
		}
	}

	protected Map<K, V> wrapped;

	public NoOverwriteMap(Map<K, V> wrap)
	{
		this.wrapped = wrap;
	}

	/**
	 */
	@Override
	public int size()
	{
		return wrapped.size();
	}

	@Override
	public boolean isEmpty()
	{
		return wrapped.isEmpty();
	}

	@Override
	public boolean containsKey(Object key)
	{
		return wrapped.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value)
	{
		return wrapped.containsValue(value);
	}

	@Override
	public V get(Object key)
	{
		return wrapped.get(key);
	}

	@Override
	public V remove(Object key)
	{
		return wrapped.remove(key);
	}

	@Override
	public void clear()
	{
		wrapped.clear();
	}

	@Override
	public Set<K> keySet()
	{
		return wrapped.keySet();
	}

	@Override
	public Collection<V> values()
	{
		return wrapped.values();
	}

	@Override
	public Set<Map.Entry<K, V>> entrySet()
	{
		return wrapped.entrySet();
	}

	@Override
	public boolean equals(Object o)
	{
		return wrapped.equals(o);
	}

	@Override
	public int hashCode()
	{
		return wrapped.hashCode();
	}

	/**
	 * Prevents overwriting existing keys.
	 * 
	 * @throws OverwriteException
	 *             if the key is already present.
	 */
	@Override
	public V put(K key, V value)
	{
		if (this.containsKey(key))
			throw new OverwriteException(key);

		return this.wrapped.put(key, value);
	}

	/**
	 * Prevents overwriting existing keys.
	 * 
	 * @throws OverwriteException
	 *             if the key is already present.
	 */
	@Override
	public void putAll(Map< ? extends K, ? extends V> t)
	{
		for (Iterator< ? extends Map.Entry< ? extends K, ? extends V>> i = t.entrySet().iterator(); i
			.hasNext();)
		{
			Map.Entry< ? extends K, ? extends V> e = i.next();
			this.put(e.getKey(), e.getValue());
		}
	}
}
