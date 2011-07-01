/*
 * $Id$ $Revision:
 * 1.1 $ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.core.parameters;

import nl.openedge.gaps.core.ModelException;

/**
 * Exception die wordt gegooid indien een parameter niet kan worden opgeslagen.
 */
public class SaveException extends ModelException
{
    /** serial UUID. */
	private static final long serialVersionUID = 8563537895295086036L;

    /**
	 * Construct.
	 */
	public SaveException()
	{
		super();
	}

	/**
	 * Construct.
	 * @param message message
	 */
	public SaveException(String message)
	{
		super(message);
	}

	/**
	 * Construct.
	 * @param cause cause
	 */
	public SaveException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * Construct.
	 * @param message message
	 * @param cause cause
	 */
	public SaveException(String message, Throwable cause)
	{
		super(message, cause);
	}

}