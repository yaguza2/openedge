package nl.openedge.baritus.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.*;

/**
 * A <code>MultiMap</code> is a Map with slightly different semantics. Putting a value
 * into the map will add the value to a Collection at that key. Getting a value will
 * always return a Collection, holding all the values put to that key. This implementation
 * uses an ArrayList as the collection.
 * <p>
 * For example:
 * 
 * <pre>
 * MultiMap mhm = new MultiHashMap();
 * mhm.put(key, &quot;A&quot;);
 * mhm.put(key, &quot;B&quot;);
 * mhm.put(key, &quot;C&quot;);
 * Collection coll = mhm.get(key);
 * </pre>
 * <p>
 * <code>coll</code> will be a list containing "A", "B", "C".
 * 
 * NOTE: copied this from Commons Collections, as this is the only class used from the
 * Collections package, and it's for internal use.
 * 
 * Commons Collections 2.0 Revision: 1.11 $ $Date: 2003/05/16 14:40:56
 * 
 * @author Christopher Berry
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @author Steve Downey
 * @author Stephen Colebourne
 * @author <a href="mailto:jburet@yahoo.com">Julien Buret</a>
 * @author Serhiy Yevtushenko
 */
@SuppressWarnings("all")
public class MultiHashMap extends HashMap implements Map
{

	private static final long serialVersionUID = 1L;

	// backed values collection
	private transient Collection values = null;

	/**
	 * Constructor.
	 */
	public MultiHashMap()
	{
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param initialCapacity
	 *            the initial map capacity
	 */
	public MultiHashMap(int initialCapacity)
	{
		super(initialCapacity);
	}

	/**
	 * Constructor.
	 * 
	 * @param initialCapacity
	 *            the initial map capacity
	 * @param loadFactor
	 *            the amount 0.0-1.0 at which to resize the map
	 */
	public MultiHashMap(int initialCapacity, float loadFactor)
	{
		super(initialCapacity, loadFactor);
	}

	/**
	 * Constructor.
	 * 
	 * @param mapToCopy
	 *            a Map to copy
	 */
	public MultiHashMap(Map mapToCopy)
	{
		super(mapToCopy);
	}

	/**
	 * Read the object during deserialization.
	 */
	private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException
	{
		// This method is needed because the 1.2/1.3 Java deserialisation called
		// put and thus messed up that method

		// default read object
		s.defaultReadObject();

		// problem only with jvm <1.4
		String version = "1.2";
		try
		{
			version = System.getProperty("java.version");
		}
		catch (SecurityException ex)
		{
			// ignore and treat as 1.2/1.3
		}

		if (version.startsWith("1.2") || version.startsWith("1.3"))
		{
			for (Iterator iterator = entrySet().iterator(); iterator.hasNext();)
			{
				Map.Entry entry = (Map.Entry) iterator.next();
				// put has created a extra collection level, remove it
				super.put(entry.getKey(), ((Collection) entry.getValue()).iterator().next());
			}
		}
	}

	/**
	 * Put a key and value into the map.
	 * <p>
	 * The value is added to a collection mapped to the key instead of replacing the
	 * previous value.
	 * 
	 * @param key
	 *            the key to set
	 * @param value
	 *            the value to set the key to
	 * @return the value added if the add is successful, <code>null</code> otherwise
	 */
	@Override
	public Object put(Object key, Object value)
	{
		// NOTE:: put is called during deserialization in JDK < 1.4 !!!!!!
		// so we must have a readObject()
		Collection coll = (Collection) super.get(key);
		if (coll == null)
		{
			coll = createCollection(null);
			super.put(key, coll);
		}
		boolean results = coll.add(value);

		return (results ? value : null);
	}

	/**
	 * Does the map contain a specific value.
	 * <p>
	 * This searches the collection mapped to each key, and thus could be slow.
	 * 
	 * @param value
	 *            the value to search for
	 * @return true if the list contains the value
	 */
	@Override
	public boolean containsValue(Object value)
	{
		Set pairs = super.entrySet();

		if (pairs == null)
		{
			return false;
		}
		Iterator pairsIterator = pairs.iterator();
		while (pairsIterator.hasNext())
		{
			Map.Entry keyValuePair = (Map.Entry) pairsIterator.next();
			Collection coll = (Collection) keyValuePair.getValue();
			if (coll.contains(value))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Removes a specific value from map.
	 * <p>
	 * The item is removed from the collection mapped to the specified key.
	 * 
	 * @param key
	 *            the key to remove from
	 * @param value
	 *            the value to remove
	 * @return the value removed (which was passed in)
	 */
	public Object remove(Object key, Object value)
	{
		Collection valuesForKey = (Collection) super.get(key);
		if (valuesForKey == null)
		{
			return null;
		}
		valuesForKey.remove(value);

		// remove the list if it is now empty
		// (saves space, and allows equals to work)
		if (valuesForKey.isEmpty())
		{
			remove(key);
		}
		return value;
	}

	/**
	 * Clear the map.
	 * <p>
	 * This clears each collection in the map, and so may be slow.
	 */
	@Override
	public void clear()
	{
		// For gc, clear each list in the map
		Set pairs = super.entrySet();
		Iterator pairsIterator = pairs.iterator();
		while (pairsIterator.hasNext())
		{
			Map.Entry keyValuePair = (Map.Entry) pairsIterator.next();
			Collection coll = (Collection) keyValuePair.getValue();
			coll.clear();
		}
		super.clear();
	}

	/**
	 * Gets a view over all the values in the map.
	 * <p>
	 * The values view includes all the entries in the collections at each map key.
	 * 
	 * @return the collection view of all the values in the map
	 */
	@Override
	public Collection values()
	{
		Collection vs = values;
		return (vs != null ? vs : (values = new Values()));
	}

	/**
	 * Inner class to view the elements.
	 */
	private class Values extends AbstractCollection
	{

		@Override
		public Iterator iterator()
		{
			return new ValueIterator();
		}

		@Override
		public int size()
		{
			int compt = 0;
			Iterator it = iterator();
			while (it.hasNext())
			{
				it.next();
				compt++;
			}
			return compt;
		}

		@Override
		public void clear()
		{
			MultiHashMap.this.clear();
		}

	}

	/**
	 * Inner iterator to view the elements.
	 */
	private class ValueIterator implements Iterator
	{
		private Iterator backedIterator;

		private Iterator tempIterator;

		private ValueIterator()
		{
			backedIterator = MultiHashMap.super.values().iterator();
		}

		private boolean searchNextIterator()
		{
			while (tempIterator == null || tempIterator.hasNext() == false)
			{
				if (backedIterator.hasNext() == false)
				{
					return false;
				}
				tempIterator = ((Collection) backedIterator.next()).iterator();
			}
			return true;
		}

		@Override
		public boolean hasNext()
		{
			return searchNextIterator();
		}

		@Override
		public Object next()
		{
			if (searchNextIterator() == false)
			{
				throw new NoSuchElementException();
			}
			return tempIterator.next();
		}

		@Override
		public void remove()
		{
			if (tempIterator == null)
			{
				throw new IllegalStateException();
			}
			tempIterator.remove();
		}

	}

	/**
	 * Clone the map.
	 * <p>
	 * The clone will shallow clone the collections as well as the map.
	 * 
	 * @return the cloned map
	 */
	@Override
	public Object clone()
	{
		MultiHashMap obj = (MultiHashMap) super.clone();

		// clone each Collection container
		for (Iterator it = entrySet().iterator(); it.hasNext();)
		{
			Map.Entry entry = (Map.Entry) it.next();
			Collection coll = (Collection) entry.getValue();
			Collection newColl = createCollection(coll);
			entry.setValue(newColl);
		}
		return obj;
	}

	/**
	 * Creates a new instance of the map value Collection container.
	 * <p>
	 * This method can be overridden to use your own collection type.
	 * 
	 * @param coll
	 *            the collection to copy, may be null
	 * @return the new collection
	 */
	protected Collection createCollection(Collection coll)
	{
		if (coll == null)
		{
			return new ArrayList();
		}
		else
		{
			return new ArrayList(coll);
		}
	}

}
