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

public class MockClass implements Serializable
{
	private static final long serialVersionUID = 1L;

	private int id;

	public int getId()
	{
		return id;
	}

	public void setId(final int i)
	{
		id = i;
	}
}