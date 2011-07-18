/*
 * $Id$
 * $Revision$
 * $Date$
 *
 * ================================================================================
 * Copyright (c) 2004, Open Edge B.V.,  All Rights Reserved.
 */
package nl.openedge.util.hibernate;

/**
 * Configuration exception.
 */
public final class ConfigException extends Exception
{

	/**
	 * Construct.
	 */
	public ConfigException()
	{
		super();
	}

	/**
	 * Construct.
	 * 
	 * @param message
	 *            message
	 */
	public ConfigException(String message)
	{
		super(message);
	}

	/**
	 * Construct.
	 * 
	 * @param cause
	 *            cause
	 */
	public ConfigException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * Construct.
	 * 
	 * @param message
	 *            message
	 * @param cause
	 *            cause
	 */
	public ConfigException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
