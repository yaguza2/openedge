/*
 * $Id$
 * $Revision$ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.support;

import nl.openedge.gaps.core.ModelException;

/**
 * Wordt gegooid door de ParameterBuilder.
 */
public class ParameterBuilderException extends ModelException
{
    /** serial UUID. */
	private static final long serialVersionUID = 403982578729053436L;

    /**
	 * Construct.
	 */
	public ParameterBuilderException()
	{
		super();
	}

	/**
	 * Construct.
	 * @param message message
	 */
	public ParameterBuilderException(String message)
	{
		super(message);
	}

	/**
	 * Construct.
	 * @param cause cause
	 */
	public ParameterBuilderException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * Construct.
	 * @param message message
	 * @param cause cause
	 */
	public ParameterBuilderException(String message, Throwable cause)
	{
		super(message, cause);
	}

}