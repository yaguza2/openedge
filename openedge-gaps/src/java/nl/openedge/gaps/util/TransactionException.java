/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ================================================================================
 * Copyright (c)
 * All rechten voorbehouden.
 */
package nl.openedge.gaps.util;

import nl.openedge.gaps.core.UncheckedModelException;

/**
 * Exception voor gebruik met TransactionUtil.
 */
public class TransactionException extends UncheckedModelException
{

	/**
	 * Construct.
	 */
	public TransactionException()
	{
		super();
	}

	/**
	 * Construct.
	 * @param message message
	 */
	public TransactionException(String message)
	{
		super(message);
	}

	/**
	 * Construct.
	 * @param cause cause
	 */
	public TransactionException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * Construct.
	 * @param message message
	 * @param cause cause
	 */
	public TransactionException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
