/*
 * $Id$
 * $Revision$ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.core;

/**
 * Basisklasse voor unchecked exceptions.
 */
public abstract class UncheckedModelException extends RuntimeException
{

	/**
	 * Construct.
	 */
	public UncheckedModelException()
	{
		super();
	}

	/**
	 * Construct.
	 * @param message message
	 */
	public UncheckedModelException(String message)
	{
		super(message);
	}

	/**
	 * Construct.
	 * @param cause cause
	 */
	public UncheckedModelException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * Construct.
	 * @param message message
	 * @param cause cause
	 */
	public UncheckedModelException(String message, Throwable cause)
	{
		super(message, cause);
	}

}