/*
 * $Id$ $Revision:
 * 1.1 $ $Date$
 * ================================================================================
 * Copyright (c) All rechten voorbehouden.
 */
package nl.openedge.gaps.core.parameters;

import nl.openedge.gaps.core.ModelException;

/**
 * Exception die wordt gegooid indien aangeboden string waarde niet kan worden vertaald
 * naar een geldige invoer.
 */
public class InputException extends ModelException
{
    /** serial UUID. */
	private static final long serialVersionUID = 5273363844911289697L;

    /**
	 * Construct.
	 */
	public InputException()
	{
		super();
	}

	/**
	 * Construct.
	 * @param message message
	 */
	public InputException(String message)
	{
		super(message);
	}

	/**
	 * Construct.
	 * @param cause cause
	 */
	public InputException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * Construct.
	 * @param message message
	 * @param cause cause
	 */
	public InputException(String message, Throwable cause)
	{
		super(message, cause);
	}

}