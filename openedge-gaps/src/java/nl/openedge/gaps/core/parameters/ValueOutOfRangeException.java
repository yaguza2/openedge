/*
 * $Id$
 * $Revision$ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.core.parameters;

/**
 * Exception die wordt gegooid als een gegeven invoer binnen een toegestane range of set
 * valt.
 */
public class ValueOutOfRangeException extends InputException
{
    /** serial UUID. */
	private static final long serialVersionUID = 3832608304534188269L;

    /**
	 * Construct.
	 */
	public ValueOutOfRangeException()
	{
		super();
	}

	/**
	 * Construct.
	 * @param message message
	 */
	public ValueOutOfRangeException(String message)
	{
		super(message);
	}

	/**
	 * Construct.
	 * @param cause cause
	 */
	public ValueOutOfRangeException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * Construct.
	 * @param message message
	 * @param cause cause
	 */
	public ValueOutOfRangeException(String message, Throwable cause)
	{
		super(message, cause);
	}

}