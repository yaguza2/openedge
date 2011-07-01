/*
 * $Id$
 * $Revision$ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.core;

/**
 * Exception die kan worden gegooid indien een entiteit niet kan worden gevonden.
 */
public class NotFoundException extends ModelException
{

	/**
	 * Construct.
	 */
	public NotFoundException()
	{
		super();
	}

	/**
	 * Construct.
	 * @param message message
	 */
	public NotFoundException(String message)
	{
		super(message);
	}

	/**
	 * Construct.
	 * @param cause cause
	 */
	public NotFoundException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * Construct.
	 * @param message message
	 * @param cause cause
	 */
	public NotFoundException(String message, Throwable cause)
	{
		super(message, cause);
	}

}