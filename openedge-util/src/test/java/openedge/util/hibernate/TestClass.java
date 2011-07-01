/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ================================================================================
 * Copyright (c) 2004, Open Edge B.V.,  All Rights Reserved.
 */
package nl.openedge.util.hibernate;

import java.io.Serializable;

/**
 */
public class TestClass implements Serializable
{

	/** test id. */
	private int id;

	/**
	 * Get id.
	 * 
	 * @return int id
	 */
	public int getId()
	{
		return id;
	}

	/**
	 * Zet id.
	 * 
	 * @param i
	 *            id
	 */
	public void setId(final int i)
	{
		id = i;
	}

}