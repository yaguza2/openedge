/*
 * $Id$
 * $Revision$ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.core.groups.impl;

import nl.openedge.gaps.core.ModelException;

/**
 * Exceptie voor {@link GroupDAO}.
 */
public final class GroupDAOException extends ModelException
{
    /** serial UUID. */
	private static final long serialVersionUID = -2788524624115419696L;

    /**
	 * Construct.
	 */
	public GroupDAOException()
	{
		super();
	}

	/**
	 * Construct.
	 * @param message message
	 */
	public GroupDAOException(String message)
	{
		super(message);
	}

	/**
	 * Construct.
	 * @param cause cause
	 */
	public GroupDAOException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * Construct.
	 * @param message message
	 * @param cause cause
	 */
	public GroupDAOException(String message, Throwable cause)
	{
		super(message, cause);
	}

}