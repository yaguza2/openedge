/*
 * $Id$
 * $Revision$ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.core.versions.impl;

import nl.openedge.gaps.core.ModelException;

/**
 * Exceptie voor {@link VersionDAO}.
 */
public final class VersionDAOException extends ModelException
{

	/**
	 * Construct.
	 */
	public VersionDAOException()
	{
		super();
	}

	/**
	 * Construct.
	 * @param message message
	 */
	public VersionDAOException(String message)
	{
		super(message);
	}

	/**
	 * Construct.
	 * @param cause cause
	 */
	public VersionDAOException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * Construct.
	 * @param message message
	 * @param cause cause
	 */
	public VersionDAOException(String message, Throwable cause)
	{
		super(message, cause);
	}

}