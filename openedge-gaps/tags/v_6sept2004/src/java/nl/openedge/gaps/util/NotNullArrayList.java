/*
 * $Id$
 * $Revision$ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.util;

import java.util.ArrayList;
import java.util.Collection;

/**
 * ArrayList die geen null toevoegingen toestaat.
 */
public class NotNullArrayList extends ArrayList
{

	/**
	 * Construct.
	 */
	public NotNullArrayList()
	{
		super();
	}

	/**
	 * Construct.
	 * @param initialCapacity initiele capaciteit
	 */
	public NotNullArrayList(int initialCapacity)
	{
		super(initialCapacity);
	}

	/**
	 * Construct.
	 * @param c collection
	 */
	public NotNullArrayList(Collection c)
	{
		super(c);
	}

	/**
	 * @see java.util.List#add(int, java.lang.Object)
	 */
	public void add(int index, Object element)
	{
		if (element == null)
		{
			throw new NullPointerException("null waarden toevoegen is niet toegestaan");
		}
		super.add(index, element);
	}

	/**
	 * @see java.util.Collection#add(java.lang.Object)
	 */
	public boolean add(Object o)
	{
		if (o == null)
		{
			throw new NullPointerException("null waarden toevoegen is niet toegestaan");
		}
		return super.add(o);
	}

	/**
	 * @see java.util.Collection#addAll(java.util.Collection)
	 */
	public boolean addAll(Collection c)
	{
		if (c == null)
		{
			throw new NullPointerException("null waarden toevoegen is niet toegestaan");
		}
		return super.addAll(c);
	}

	/**
	 * @see java.util.List#addAll(int, java.util.Collection)
	 */
	public boolean addAll(int index, Collection c)
	{
		if (c == null)
		{
			throw new NullPointerException("null waarden toevoegen is niet toegestaan");
		}
		return super.addAll(index, c);
	}
}