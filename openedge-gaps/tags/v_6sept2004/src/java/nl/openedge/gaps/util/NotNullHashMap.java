/*
 * $Id$ $Revision:
 * 1.1 $ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.util;

import java.util.HashMap;
import java.util.Map;

/**
 * HashMap die geen null toevoegingen toestaat.
 */
public class NotNullHashMap extends HashMap
{

	/**
	 * Construct.
	 */
	public NotNullHashMap()
	{
		super();
	}

	/**
	 * Construct.
	 * @param initialCapacity initiele capaciteit
	 */
	public NotNullHashMap(int initialCapacity)
	{
		super(initialCapacity);
	}

	/**
	 * Construct.
	 * @param initialCapacity initiele capaciteit
	 * @param loadFactor load factor
	 */
	public NotNullHashMap(int initialCapacity, float loadFactor)
	{
		super(initialCapacity, loadFactor);
	}

	/**
	 * Construct.
	 * @param m map
	 */
	public NotNullHashMap(Map m)
	{
		super(m);
	}

	/**
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	public Object put(Object key, Object value)
	{
		if (value == null)
		{
			throw new NullPointerException("null waarden toevoegen is niet toegestaan");
		}
		return super.put(key, value);
	}

	/**
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	public void putAll(Map m)
	{
		if (m == null)
		{
			throw new NullPointerException("null waarden toevoegen is niet toegestaan");
		}
		super.putAll(m);
	}
}