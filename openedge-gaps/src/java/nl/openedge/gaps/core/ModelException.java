/*
 * $Id$ $Revision:
 * 1.1 $ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.core;

/**
 * Basisklasse voor checked exceptions.
 */
public abstract class ModelException extends Exception
{

	/**
	 * Construct.
	 */
	public ModelException()
	{
		super();
	}

	/**
	 * Construct.
	 * @param message message
	 */
	public ModelException(String message)
	{
		super(message);
	}

	/**
	 * Construct.
	 * @param cause cause
	 */
	public ModelException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * Construct.
	 * @param message message
	 * @param cause cause
	 */
	public ModelException(String message, Throwable cause)
	{
		super(message, cause);
	}

}