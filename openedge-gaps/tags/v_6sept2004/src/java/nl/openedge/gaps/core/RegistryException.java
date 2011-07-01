/*
 * $Id$
 * $Revision$ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.core;

/**
 * Exception die kan worden gegooid indien een registry een gevraagde actie niet naar
 * behoren heeft kunnen uitvoeren (bijv als een repository niet beschikbaar is).
 */
public class RegistryException extends UncheckedModelException
{

	/**
	 * Construct.
	 */
	public RegistryException()
	{
		super();
	}

	/**
	 * Construct.
	 * @param message message
	 */
	public RegistryException(String message)
	{
		super(message);
	}

	/**
	 * Construct.
	 * @param cause cause
	 */
	public RegistryException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * Construct.
	 * @param message message
	 * @param cause cause
	 */
	public RegistryException(String message, Throwable cause)
	{
		super(message, cause);
	}

}